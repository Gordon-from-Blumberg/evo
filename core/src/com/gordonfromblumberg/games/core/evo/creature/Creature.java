package com.gordonfromblumberg.games.core.evo.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.model.GameObject;
import com.gordonfromblumberg.games.core.evo.food.Food;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;
import com.gordonfromblumberg.games.core.evo.physics.CreatureMovingStrategy;
import com.gordonfromblumberg.games.core.evo.state.State;
import com.gordonfromblumberg.games.core.evo.world.SpawnPoint;

import java.util.Iterator;

public class Creature extends EvoGameObject {

    private static final Pool<Creature> pool = new Pool<Creature>() {
        @Override
        protected Creature newObject() {
            return new Creature();
        }
    };
    private static final float BASE_SATIETY = AbstractFactory.getInstance().configManager().getFloat("game.creature.satiety");
    private static final float BASE_SENSE_RADIUS = AbstractFactory.getInstance().configManager().getFloat("game.creature.sense");
    private static final float BASE_FORWARD_VELOCITY = 5;
    private static final float BASE_BACKWARD_VELOCITY = 2;
    private static final float BASE_ANGLE_VELOCITY = 180;
    private static final float BASE_MAX_ROTATION = 250;
    private static final float BASE_ACCELERATION = 5;
    private static final float BASE_DECELERATION = 6;

    private final DNA dna = new DNA();
    private final IntMap[] stateParams = new IntMap[State.values().length];
    private int generation;
    private Creature parent;

    private State state;
    private boolean isPredator;
    private EvoGameObject target;
    private float senseRadius;
    private float acceleration;
    private float requiredSatiety, offspringSatiety, satiety;
    private int offspringCount, offspringProduced;
    private SpawnPoint spawnPoint;

    private Creature() {
        movingStrategy = new CreatureMovingStrategy();
    }

    public static Creature getInstance() {
        return pool.obtain();
    }

    public void init(int generation) {
        this.generation = generation;
        isPredator = dna.getFoodType() == DNA.FoodType.PREDATOR;
        setRegion(isPredator ? "predator" : "herbivorous");
        float size = 1 + 0.1f * dna.getSize();
        setSize(size, size);
        senseRadius = (1 + 0.1f * dna.getSense()) * BASE_SENSE_RADIUS * size;
        requiredSatiety = calcRequiredSatiety();
        offspringSatiety = calcOffspringSatiety();
        offspringCount = 1;
        offspringProduced = 0;
        satiety = 0;

        float velocityCoef = 1 + 0.1f * dna.getVelocity();
        CreatureMovingStrategy ms = (CreatureMovingStrategy) movingStrategy;
        ms.setMaxVelocityForward(velocityCoef * size * BASE_FORWARD_VELOCITY);
        ms.setMaxVelocityBackward(velocityCoef * size * BASE_BACKWARD_VELOCITY);

        ms.setMaxAngleVelocity((1 + 0.05f * dna.getSize()) * BASE_ANGLE_VELOCITY);
        ms.setMaxRotation((1 + 0.05f * dna.getSize()) * BASE_MAX_ROTATION);

        acceleration = (1 - 0.1f * dna.getSize()) * BASE_ACCELERATION;
        ms.setMaxAcceleration(acceleration);
        ms.setMaxDeceleration((1 - 0.1f * dna.getSize()) * BASE_DECELERATION);
    }

    @Override
    public void update(float delta) {
        if (state == null)
            setState(State.WAITING);
        state.update(this, delta);
        super.update(delta);
    }

    public void setTarget(float x, float y) {
        if (movingStrategy == null) {
            movingStrategy = new CreatureMovingStrategy();
        }

        ((CreatureMovingStrategy) movingStrategy).setTarget(x, y);
    }

    public void eat(EvoGameObject foodObject) {
        // TODO for predators
        if (foodObject instanceof Food) {
            Food food = (Food) foodObject;
            satiety += food.getValue();

            Iterator<Creature> chaserIt = food.getChasers().iterator();
            while(chaserIt.hasNext()) {
                Creature chaser = chaserIt.next();
                chaserIt.remove();
                chaser.setTarget(null);
            }

            gameWorld.removeGameObject(food);
        }
    }

    public boolean isEatable(EvoGameObject go) {
        return isPredator && go instanceof Creature
                || !isPredator && go instanceof Food;
    }

    public boolean readyToReproduce() {
        return satiety >= offspringSatiety;
    }

    public void produceOffspring(int generation) {
        int count = calcOffspringCount();
        for (int i = 0; i < count; i++) {
            Creature child = pool.obtain();
            this.dna.copy(child.dna);
            child.dna.mutate();
            child.init(generation);
            child.parent = this;
            child.spawnPoint = this.spawnPoint;
            gameWorld.addGameObject(child);
            Gdx.app.log("Creature", child.getDescription());
        }

        offspringProduced += count;
        gameWorld.offspringProduced(count);
    }

    @Override
    protected void adjustPosition() {
        Vector2 position = this.position;
        Vector2 velocity = this.velocity;
        float halfSize = getWidth() / 2;
        if (position.x < halfSize) {
            position.x = halfSize;
            if (velocity.x < 0)
                velocity.x = 0;
        }
        if (position.x > gameWorld.width - halfSize) {
            position.x = gameWorld.width - halfSize;
            if (velocity.x > 0)
                velocity.x = 0;
        }
        if (position.y < halfSize) {
            position.y = halfSize;
            if (velocity.y < 0)
                velocity.y = 0;
        }
        if (position.y > gameWorld.height - halfSize) {
            position.y = gameWorld.height - halfSize;
            if (velocity.y > 0)
                velocity.y = 0;
        }
    }

    public void setDecelerate(boolean decelerate) {
        ((CreatureMovingStrategy) movingStrategy).setDecelerate(decelerate);
    }

    public boolean isTargetReached() {
        return ((CreatureMovingStrategy) movingStrategy).isTargetReached();
    }

    public void setState(State state) {
        if (this.state != state) {
            this.state = state;
            state.enter(this);
            Gdx.app.log("State", "Creature #" + id + " entered in " + state + " state");
        }
    }

    public IntMap<Object> getStateParams(State state) {
        if (stateParams[state.ordinal()] == null)
            stateParams[state.ordinal()] = new IntMap<>();
        return stateParams[state.ordinal()];
    }

    public GameObject getTarget() {
        return target;
    }

    public void setTarget(EvoGameObject target) {
        if (this.target != null && this.target != target) {
            this.target.removeChaser(this);
        }
        this.target = target;
        if (target != null) {
            setTarget(target.position.x, target.position.y);
            target.addChaser(this);
        }
    }

    private float calcRequiredSatiety() {
        float baseReqSatiety = (float) Math.pow(size, 3);
        float reqSatietyMod = 0.1f * dna.getVelocity();
        reqSatietyMod += 0.1f * dna.getSense();
        return baseReqSatiety * (1 + reqSatietyMod) * BASE_SATIETY;
    }

    private float calcOffspringSatiety() {
        return requiredSatiety * 2;
    }

    public void setForceMultiplier(float forceMultiplier) {
        ((CreatureMovingStrategy) movingStrategy).setMaxAcceleration(forceMultiplier * acceleration);
    }

    public void resetSatiety() {
        satiety = 0;
    }

    private int calcOffspringCount() {
        return offspringCount;
    }

     public String getDescription() {
         final CreatureMovingStrategy ms = (CreatureMovingStrategy) movingStrategy;
         return toString() + " - " + dna.getDescription() + "; senseRadius = " + senseRadius
                 + "; requiredSatiety = " + requiredSatiety + "; maxVelocity = " + ms.getMaxVelocityForward()
                 + "; acceleration = " + acceleration;
     }

     public String getDnaDescription() {
        return dna.getDescription();
     }

    public void release() {
        pool.free(this);
    }

    @Override
    public void reset() {
        super.reset();
        generation = 0;
        parent = null;
        spawnPoint = null;
        satiety = 0;
    }

    public float getSenseRadius() {
        return senseRadius;
    }

    public boolean isPredator() {
        return isPredator;
    }

    public float getRequiredSatiety() {
        return requiredSatiety;
    }

    public float getOffspringSatiety() {
        return offspringSatiety;
    }

    public float getSatiety() {
        return satiety;
    }

    public SpawnPoint getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(SpawnPoint spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public int getGeneration() {
        return generation;
    }
}
