package com.gordonfromblumberg.games.core.common.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.evo.model.EvoGameObject;

public class GameWorldRenderer {
    private final GameWorld world;
    private Viewport viewport;
    private final Rectangle worldArea = new Rectangle();

    private final Color pauseColor = Color.GRAY;
    private final Color tempClr1 = new Color(), tempClr2 = new Color();

    private NinePatch background;

    public GameWorldRenderer(GameWorld world) {
        this.world = world;
    }

    public void initialize(Viewport viewport, float width, float height) {
        final AssetManager assets = Main.getInstance().assets();

        this.viewport = viewport;
        worldArea.setSize(width, height);

        background = new NinePatch(
                assets.get("image/texture_pack.atlas", TextureAtlas.class)
                        .findRegion("world-background"),
                1, 1, 1, 1
        );
    }

    public void render(Batch batch) {
        final Color origColor = tempClr1.set(batch.getColor());
        final GameWorld world = this.world;
        final Rectangle worldArea = this.worldArea;
        if (world.paused)
            batch.setColor(pauseColor);

        background.draw(batch, worldArea.x, worldArea.y, worldArea.width, worldArea.height);

        final float coordMultiplier = worldArea.width / world.width;

        if (world.paused) {
            for (EvoGameObject go : world.gameObjects) {
                final Color origGoColor = tempClr2.set(go.getColor());
                go.setColor(pauseColor);
                go.render(batch, coordMultiplier);
                go.setColor(origGoColor);
            }
        } else {
            for (EvoGameObject go : world.gameObjects) {
                go.render(batch, coordMultiplier);
            }
        }

        if (world.paused)
            batch.setColor(origColor);
    }
}
