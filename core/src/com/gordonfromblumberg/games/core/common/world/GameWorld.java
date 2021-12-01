package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.event.Event;
import com.gordonfromblumberg.games.core.common.event.EventHandler;
import com.gordonfromblumberg.games.core.common.event.EventProcessor;
import com.gordonfromblumberg.games.core.common.event.SimpleEvent;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.model.GameObject;
import com.gordonfromblumberg.games.core.common.utils.RandomUtils;
import com.gordonfromblumberg.games.core.evo.event.HomeReachedEvent;
import com.gordonfromblumberg.games.core.evo.state.State;
import com.gordonfromblumberg.games.core.evo.world.SpawnPoint;
import com.gordonfromblumberg.games.core.evo.world.WorldParams;
import com.gordonfromblumberg.games.core.evo.creature.Creature;
import com.gordonfromblumberg.games.core.evo.event.NewGenerationEvent;
import com.gordonfromblumberg.games.core.evo.food.Food;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;
import com.gordonfromblumberg.games.core.evo.physics.CreatureMovingStrategy;

import java.util.Iterator;

import static com.gordonfromblumberg.games.core.common.utils.RandomUtils.*;

public class GameWorld implements Disposable {
    private static int nextId = 1;

    public final WorldParams params = new WorldParams();
    final Array<EvoGameObject> gameObjects = new Array<>();

    private final EventProcessor eventProcessor = new EventProcessor();

    public int width, height;

    private int creatureCount, foodCount, homeReachedCount, birthedCount;
    int generation;
    int generationsToSimulate;

    final Array<SpawnPoint> spawnPoints = new Array<>(30 * 4);
    int spawnPointCount;

    boolean paused, started, stopRequested, generated;

    int maxGameObjectCount = 0;
    private float time = 0;

    public enum Direction { BOTTOM, RIGHT, TOP, LEFT }

    public void initialize(int width, int height) {
        params.setDefault();

        setSize(width, height);

        eventProcessor.registerHandler("HomeReached", e -> {
            homeReachedCount++;
            if (homeReachedCount == creatureCount) {
                eventProcessor.push(SimpleEvent.of("SimulationFinished"));
            }
            return false;
        });

        eventProcessor.registerHandler("SimulationFinished", e -> {
            finish();
            return false;
        });
    }

    public void newGeneration() {
        generation++;
        homeReachedCount = 0;

        generateFood();
        if (generation == 1)
            createFirstGeneration();
        else
            produceOffspring();
        placeCreatures();
        NewGenerationEvent event = NewGenerationEvent.getInstance();
        event.setGenerationNumber(generation);
        event.setCreatureCount(creatureCount);
        event.setBirthedCount(birthedCount);
        event.setFoodCount(foodCount);
        eventProcessor.push(event);
        generated = true;
    }

    public void generateFood() {
        Iterator<EvoGameObject> goIt = gameObjects.iterator();
        while (goIt.hasNext()) {
            EvoGameObject go = goIt.next();
            if (go instanceof Food) {
                Food food = (Food) go;
                food.release();
                goIt.remove();
            }
        }

        final float fromX = (float) width / (width / 2) + 0.5f;
        final float toX = width - fromX;
        final float fromY = (float) height / (height / 2) + 0.5f;
        final float toY = height - fromY;

        final int foodCount = nextInt(params.getFoodCountFrom(), params.getFoodCountTo());
        for (int i = 0; i < foodCount; i++) {
            Food food = Food.getInstance();
            food.setValue(nextFloat(params.getFoodValueFrom(), params.getFoodValueTo()));
            food.setPosition(nextFloat(fromX, toX), nextFloat(fromY, toY));
            food.setRegion("food");
            food.updateSize();
            addGameObject(food);
        }

        this.foodCount = foodCount;
    }

    void createFirstGeneration() {
        creatureCount = 0;
        for (int i = 0, count = params.getCreaturesCount(); i < count; i++) {
            Creature creature = Creature.getInstance();
            creature.init(1);
            addGameObject(creature);
            Gdx.app.log("Creature", creature.getDescription());
            creatureCount++;
        }
    }

    void produceOffspring() {
        birthedCount = 0;
        for (int i = 0, size = gameObjects.size; i < size; i++) {
            EvoGameObject ego;
            if ((ego = gameObjects.get(i)) instanceof Creature && ((Creature) ego).readyToReproduce()) {
                ((Creature) ego).produceOffspring(generation);
            }
        }
    }

    void placeCreatures() {
        int maxCount;

        maxCount = creatureCount / spawnPointCount + 1;
        Gdx.app.log("Spawn", "Creatures count = " + creatureCount
                + ", spawnPointCount = " + spawnPointCount + ", max count per spawn point = " + maxCount);

        for (SpawnPoint sp : spawnPoints)
            sp.clear();

        for (EvoGameObject go : gameObjects) {
            if (go instanceof Creature) {
                Creature creature = (Creature) go;
                SpawnPoint sp;

                if ((sp = creature.getSpawnPoint()) != null) {

                    sp.addCreature(creature);

                    if (Main.DEBUG)
                        Gdx.app.log("Spawn", creature + " has spawn point"
                                + " with x = " + sp.getX() + ", y = " + sp.getY());

                } else {

                    boolean placed = false;
                    while (!placed) {
                        int i = RandomUtils.nextInt(spawnPointCount);
                        sp = spawnPoints.get(i);
                        if (sp.getCreatureCount() < maxCount) {
                            sp.addCreature(creature);
                            placed = true;

                            if (Main.DEBUG)
                                Gdx.app.log("Spawn", "Spawn " + creature + " at point #" + i
                                        + " with x = " + sp.getX() + ", y = " + sp.getY());
                        }
                    }
                }

                creature.resetSatiety();
            }
        }
    }

    public void simulate(int generationNumber) {
        this.generationsToSimulate = generationNumber;
        start();
    }

    private void start() {
        if (generationsToSimulate-- > 0) {

            if (!generated) {
                newGeneration();
            }

            for (EvoGameObject go : gameObjects) {
                if (go instanceof Creature) {
                    ((Creature) go).setState(State.WAITING);
                }
            }

            this.started = true;
            eventProcessor.push(SimpleEvent.of("SimulationStarted"));
        }
    }

    public void requestStop() {
        this.stopRequested = true;
    }

    private void finish() {
        this.started = false;
        this.generated = false;

        Iterator<EvoGameObject> goIt = gameObjects.iterator();
        while (goIt.hasNext()) {
            EvoGameObject go = goIt.next();
            if (go instanceof Creature) {
                Creature creature = (Creature) go;
                if (creature.getSatiety() < creature.getRequiredSatiety()) {
                    Gdx.app.log("Creature", creature + " - " + creature.getDnaDescription() + " did not eat enough food, "
                            + "so did not survive after " + (generation - creature.getGeneration()) + " generations");
                    creature.release();
                    goIt.remove();
                    creatureCount--;
                }
            }
        }

        if (!stopRequested) {
            start();
        } else {
            stopRequested = false;
        }
    }

    void setupSpawnPoints() {
        final int countX = width / 2 - 2, countY = height / 2 - 2;
        final float pointWidth = (float) width / (countX + 2), pointHeight = (float) height / (countY + 2);
        final int count = (countX + countY) * 2;
        this.spawnPointCount = count;

        Gdx.app.log("Spawn", "Point size = " + pointWidth + ", " + pointHeight
                + ", count of points = " + count + ", for x = " + countX + ", for y = " + countY);

        for (int i = 0; i < count; i++) {
            SpawnPoint sp;
            if (i < spawnPoints.size) {
                sp = spawnPoints.get(i);
                sp.clear();
            } else {
                sp = new SpawnPoint();
                spawnPoints.add(sp);
            }

            if (i < countX) {
                sp.setSpawnArea((1 + i) * pointWidth, 0, pointWidth, pointHeight);
                sp.setDir(Direction.BOTTOM);
            } else if (i < countX + countY) {
                sp.setSpawnArea((countX + 1) * pointWidth, (1 + i - countX) * pointHeight, pointWidth, pointHeight);
                sp.setDir(Direction.RIGHT);
            } else if (i < 2 * countX + countY) {
                sp.setSpawnArea((2 * countX + countY - i) * pointWidth, (countY + 1) * pointHeight, pointWidth, pointHeight);
                sp.setDir(Direction.TOP);
            } else {
                sp.setSpawnArea(0, (2 * countX + 2 * countY - i) * pointHeight, pointWidth, pointHeight);
                sp.setDir(Direction.LEFT);
            }
        }
    }

    public void setSize(int worldWidth, int worldHeight) {
        width = worldWidth;
        height = worldHeight;

        setupSpawnPoints();
    }

    public void addGameObject(EvoGameObject gameObject) {
        gameObjects.add(gameObject);
        gameObject.setGameWorld(this);
        gameObject.setActive(true);
        gameObject.setId(nextId++);

        if (gameObjects.size > maxGameObjectCount)
            maxGameObjectCount = gameObjects.size;
    }

    public void offspringProduced(int count) {
        creatureCount += count;
        birthedCount += count;
    }

    public void removeGameObject(EvoGameObject gameObject) {
        gameObjects.removeValue(gameObject, true);
        gameObject.release();

        if (gameObject instanceof Food) {
            foodCount--;
            // TODO when predators appear this condition become incorrect
            if (foodCount == 0 && started) {
                eventProcessor.push(SimpleEvent.of("SimulationFinished"));
            }
        }
    }

    public void update(float delta) {
        if (started && !paused) {
            time += delta;

            for (EvoGameObject go : gameObjects) {
                go.update(delta);
            }

            if (time > 2) {
                time = 0;
                Gdx.app.log("GameWorld", "Game objects: current count = " + gameObjects.size + ", max count = " + maxGameObjectCount);
            }
        }

        eventProcessor.process();
    }

    public Array<EvoGameObject> getGameObjects() {
        return gameObjects;
    }

    public boolean pause() {
        return this.paused = !this.paused;
    }

    public void registerHandler(String type, EventHandler handler) {
        eventProcessor.registerHandler(type, handler);
    }

    public void pushEvent(Event event) {
        eventProcessor.push(event);
    }

    @Override
    public void dispose() {
        for (GameObject gameObject : gameObjects) {
            gameObject.dispose();
        }
    }

    public int getSpawnPointCount() {
        return spawnPointCount;
    }

    public Array<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }
}
