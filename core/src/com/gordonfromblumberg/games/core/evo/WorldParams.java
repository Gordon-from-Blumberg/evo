package com.gordonfromblumberg.games.core.evo;

public class WorldParams {
    private int foodCountFrom, foodCountTo;
    private float foodValueFrom, foodValueTo;

    public WorldParams() {
        foodCountFrom = foodCountTo = 2;
        foodValueFrom = foodValueTo = 10;
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
}
