package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SpriteBatchRenderer extends AbstractRenderer {
    private SpriteBatch batch;

    public SpriteBatchRenderer(SpriteBatch batch, Camera camera, Viewport viewport) {
        super(camera, viewport);

        this.batch = batch;
    }

    @Override
    public void render(float dt) {

    }
}
