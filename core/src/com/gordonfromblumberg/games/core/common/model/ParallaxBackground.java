package com.gordonfromblumberg.games.core.common.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

public class ParallaxBackground {
    private final Array<Layer> layers = new Array<>();
    private float nextCoef = 1;

    public void addLayer() {
        layers.insert(0, new Layer(nextCoef));
        nextCoef /= 2;
    }

    public void translate(float dx, float dy) {
        for (Layer layer : layers) {
            final float coef = layer.coefficient;
            for (GameObject object : layer.gameObjects) {
                float x = object.position.x;
                float y = object.position.y;
                object.setPosition(x - dx * coef, y - dy * coef);
            }
        }
    }

//    public void render(Batch batch) {
//        for (Layer layer : layers)
//            for (GameObject object : layer.gameObjects)
//                object.render(batch);
//    }

    private static class Layer {
        private final float coefficient;
        private final Array<GameObject> gameObjects = new Array<>();

        private Layer(float coefficient) {
            this.coefficient = coefficient;
        }
    }
}
