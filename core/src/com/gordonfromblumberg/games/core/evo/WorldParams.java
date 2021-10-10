package com.gordonfromblumberg.games.core.evo;

import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;

public class WorldParams {
    private int foodCountFrom, foodCountTo;
    private float foodValueFrom, foodValueTo;
    private int creaturesCount;
    private int generationCount;

    public void setDefault() {
        ConfigManager config = AbstractFactory.getInstance().configManager();
        foodCountFrom = config.getInteger("game.world.food.count.from");
        foodCountTo = config.getInteger("game.world.food.count.to");
        foodValueFrom = config.getFloat("game.world.food.value.from");
        foodValueTo = config.getFloat("game.world.food.value.from");
        creaturesCount = config.getInteger("game.world.creatures.count");
        generationCount = config.getInteger("game.world.generation.count");
    }

    public int getFoodCountFrom() {
        return foodCountFrom;
    }

    public void setFoodCountFrom(int foodCountFrom) {
        this.foodCountFrom = foodCountFrom;
    }

    public int getFoodCountTo() {
        return foodCountTo;
    }

    public void setFoodCountTo(int foodCountTo) {
        this.foodCountTo = foodCountTo;
    }

    public float getFoodValueFrom() {
        return foodValueFrom;
    }

    public void setFoodValueFrom(float foodValueFrom) {
        this.foodValueFrom = foodValueFrom;
    }

    public float getFoodValueTo() {
        return foodValueTo;
    }

    public void setFoodValueTo(float foodValueTo) {
        this.foodValueTo = foodValueTo;
    }

    public int getCreaturesCount() {
        return creaturesCount;
    }

    public void setCreaturesCount(int creaturesCount) {
        this.creaturesCount = creaturesCount;
    }

    public int getGenerationCount() {
        return generationCount;
    }

    public void setGenerationCount(int generationCount) {
        this.generationCount = generationCount;
    }
}
