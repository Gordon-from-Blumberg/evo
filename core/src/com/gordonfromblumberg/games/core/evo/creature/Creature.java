package com.gordonfromblumberg.games.core.evo.creature;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import com.gordonfromblumberg.games.core.common.model.GameObject;
import com.gordonfromblumberg.games.core.evo.food.Food;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;
import com.gordonfromblumberg.games.core.evo.physics.CreatureMovingStrategy;
import com.gordonfromblumberg.games.core.evo.state.State;

public class Creature extends EvoGameObject {

    private static final Pool<Creature> pool = new Pool<Creature>() {
        @Override
        protected Creature newObject() {
            return new Creature();
        }
    };
    private static final float BASE_SATIETY = 10;
    private static final float BASE_SENSE_RADIUS = 5;

    private final DNA dna = new DNA();
    private final IntMap[] stateParams = new IntMap[State.values().length];

    private State state;
    private boolean isPredator;
    private EvoGameObject target;
    private float senseRadius;
    private float forceMultiplier;
    private float size;
    private float requiredSatiety, offspringSatiety, satiety;

    private Creature() {
        movingStrategy = new CreatureMovingStrategy();
    }

    public static Creature getInstance() {
        return pool.obtain();
    }

    public void init() {
        isPredator = dna.getFoodType() == DNA.FoodType.PREDATOR;
        size = dna.getSize();
        senseRadius = dna.getSense() * BASE_SENSE_RADIUS;
        requiredSatiety = calcRequiredSatiety();
        offspringSatiety = calcOffspringSatiety();
        satiety = 0;
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
            gameWorld.removeGameObject(food);
        }
    }

    public boolean isEatable(EvoGameObject go) {
        return isPredator && go instanceof Creature
                || !isPredator && go instanceof Food;
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

    public void setMaxVelocityForward(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxVelocityForward(value);
    }

    public void setMaxVelocityBackward(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxVelocityBackward(value);
    }

    public void setMaxAngleVelocity(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxAngleVelocity(value);
    }

    public void setMaxRotation(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxRotation(value);
    }

    public void setMaxAcceleration(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxAcceleration(value);
    }

    public void setMaxDeceleration(float value) {
        ((CreatureMovingStrategy) movingStrategy).setMaxDeceleration(value);
    }

    public void setDecelerationDist(float value) {
        ((CreatureMovingStrategy) movingStrategy).setDecelerationDistance(value);
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
        float baseReqSatiety = (float) Math.pow(dna.getSize(), 3);
        float reqSatietyMod = dna.getVelocityMod();
        reqSatietyMod += dna.getSenseMod();
        return baseReqSatiety * (1 + reqSatietyMod) * BASE_SATIETY;
    }

    private float calcOffspringSatiety() {
        return requiredSatiety * 2;
    }

    public float getSenseRadius() {
        return senseRadius;
    }

    public boolean isPredator() {
        return isPredator;
    }

    public float getForceMultiplier() {
        return forceMultiplier;
    }

    public void setForceMultiplier(float forceMultiplier) {
        this.forceMultiplier = forceMultiplier;
    }

    @Override
    public float getSize() {
        return size;
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

    public void release() {
        pool.free(this);
    }
}
