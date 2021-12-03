package com.gordonfromblumberg.games.core.evo.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
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

    private enum Param {
        SIZE(1, 0f, 0.1f, 0f),
        SENSE(getConfigF("game.creature.sense"), 0.075f, 0.1f, 0.1f),
        ACCELERATION(getConfigF("game.creature.acceleration"), 0.1f, 0.1f, 0.1f),
        DECELERATION(getConfigF("game.creature.deceleration"), -0.05f, 0.05f, 0f),
        DECELERATION_DIST(getConfigF("game.creature.deceleration_distance"), 0.1f, 0f, 0f),
        BACKWARD_VELOCITY(getConfigF("game.creature.backward_velocity"), 0.075f, 0.1f, 0f),
        ANGLE_VELOCITY(getConfigF("game.creature.angle_velocity"), -0.075f, 0.1f, 0f),
        MAX_ROTATION(getConfigF("game.creature.max_rotation"), -0.075f, 0.1f, 0.05f),
        FRICTION(getConfigF("game.creature.friction"), -0.075f, 0f, 0f),
        EAT_SPEED(getConfigF("game.creature.eat_speed"), 0.1f, 0.15f, 0.05f),
        ;

        private final float baseValue;
        private final float sizeMod;
        private final float geneMod;
        private final float satietyMod;

        Param(float baseValue, float sizeMod, float geneMod, float satietyMod) {
            this.baseValue = baseValue;
            this.sizeMod = sizeMod;
            this.geneMod = geneMod;
            this.satietyMod = satietyMod;
        }
    }

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
    private float eatSpeed;
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
        byte dnaSize = dna.getSize();
        float size = calcParam(Param.SIZE, dnaSize, dnaSize);
        setSize(size, size);
        senseRadius = calcParam(Param.SENSE, dnaSize, dna.getSense());
        requiredSatiety = calcRequiredSatiety();
        offspringSatiety = calcOffspringSatiety();
        offspringCount = 1;
        offspringProduced = 0;
        satiety = 0;
        eatSpeed = calcParam(Param.EAT_SPEED, dnaSize, dna.getMouthSize());

        CreatureMovingStrategy ms = (CreatureMovingStrategy) movingStrategy;
        acceleration = calcParam(Param.ACCELERATION, dnaSize, dna.getVelocity());
        ms.setMaxAcceleration(acceleration);
        ms.setMaxDeceleration(calcParam(Param.DECELERATION, dnaSize, dna.getVelocity()));
        ms.setDecelerationDistance(calcParam(Param.DECELERATION_DIST, dnaSize, (byte) 0));
        ms.setMaxVelocityBackward(calcParam(Param.BACKWARD_VELOCITY, dnaSize, dna.getVelocity()));
        ms.setMaxAngleVelocity(calcParam(Param.ANGLE_VELOCITY, dnaSize, dna.getRotation()));
        ms.setMaxRotation(calcParam(Param.MAX_ROTATION, dnaSize, dna.getRotation()));
        ms.setFriction(calcParam(Param.FRICTION, dnaSize, (byte) 0));
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

    public void eat(EvoGameObject foodObject, float dt) {
        // TODO for predators
        if (foodObject instanceof Food) {
            Food food = (Food) foodObject;

            float eaten = eatSpeed * dt;

            float leftToEat = offspringSatiety - satiety;
            if (eaten > leftToEat)
                eaten = leftToEat;

            if (eaten >= food.getValue()) {
                satiety += food.getValue();

                Iterator<Creature> chaserIt = food.getChasers().iterator();
                while(chaserIt.hasNext()) {
                    Creature chaser = chaserIt.next();
                    chaserIt.remove();
                    chaser.setTarget(null);
                }

                gameWorld.removeGameObject(food);
                return;
            }

            satiety += eaten;
            food.wasEaten(eaten);
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

    public EvoGameObject getTarget() {
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
        float reqSatietyMod = 0;
        reqSatietyMod += Param.SENSE.satietyMod * dna.getSense();
        reqSatietyMod += Param.ACCELERATION.satietyMod * dna.getVelocity();
        reqSatietyMod += Param.MAX_ROTATION.satietyMod * dna.getRotation();
        reqSatietyMod += Param.EAT_SPEED.satietyMod * dna.getMouthSize();
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

    private float calcParam(Param param, byte dnaSize, byte gene) {
        return (1 + param.sizeMod * dnaSize) * (1 + param.geneMod * gene) * param.baseValue;
    }

     public String getDescription() {
         final CreatureMovingStrategy ms = (CreatureMovingStrategy) movingStrategy;
         String desc = toString() + " - " + dna.getDescription() + ";\n\tsenseRadius = " + senseRadius
                 + "; requiredSatiety = " + requiredSatiety + "; acceleration = " + acceleration;
         if (Main.DEBUG)
             desc += ";\n\tmax velocity = " + ms.maxVelMag + ", max acceleration = " + ms.maxAccMag;
         return desc;
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
        ((CreatureMovingStrategy) movingStrategy).reset();
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

    private static float getConfigF(String propertyName) {
        return AbstractFactory.getInstance().configManager().getFloat(propertyName);
    }
}
