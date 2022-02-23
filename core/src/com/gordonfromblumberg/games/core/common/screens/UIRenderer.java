package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class UIRenderer extends AbstractRenderer {
    private Stage stage;

    public UIRenderer(Camera camera, Viewport viewport, Stage stage) {
        super(camera, viewport);

        this.stage = stage;
        this.centerCamera = true;
    }

    @Override
    public void render(float dt) {
        stage.act();
        stage.draw();
    }
}
