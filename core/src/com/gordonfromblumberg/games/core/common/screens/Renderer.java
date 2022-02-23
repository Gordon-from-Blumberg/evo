package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;

public interface Renderer {
    void render(float dt);
    void resize(int width, int height);
    Camera getCamera();
    Viewport getViewport();
}
