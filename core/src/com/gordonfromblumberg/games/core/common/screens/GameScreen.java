package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.model.GameWorld;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;
import com.gordonfromblumberg.games.core.common.utils.StringUtils;
import com.gordonfromblumberg.games.core.evo.creature.Creature;

public class GameScreen extends AbstractScreen {
    private static final String LABEL = "Mouse on ";

    TextureRegion background;
    private Label label;
    private GameWorld gameWorld;

    private final Vector3 coords = new Vector3();
    private Creature creature;

    protected GameScreen(SpriteBatch batch) {
        super(batch);

        color = Color.WHITE;
    }

    @Override
    public void show() {
        super.show();

        background = Main.getInstance().assets()
                .get("image/texture_pack.atlas", TextureAtlas.class)
                .findRegion("background");

        gameWorld = new GameWorld(viewport);

        createUI();

        stage.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                camera.zoom += amountY * 0.25;
                if (camera.zoom <= 0)
                    camera.zoom = 0.25f;
                return true;
            }
        });

        creature = Creature.getInstance();
        creature.setRegion("herbivorous");
        creature.setPosition(32, 32);
        creature.setSize(64, 64);

        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // coords don't work
//                coords.x = x;
//                coords.y = y;
//                gameWorld.convertScreenToWorld(coords);
                Gdx.app.log("Target", "Target = " + x + ", " + y);
                creature.setTarget(x, y);
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        uiViewport.update(width, height, true);
    }

    @Override
    protected void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            camera.translate(-10, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            camera.translate(10, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            camera.translate(0, 10);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            camera.translate(0, -10);

        super.update(delta);            // apply camera moving and update batch projection matrix
        gameWorld.update(delta);        // update game state
        creature.update(delta);
    }

    @Override
    protected void renderWorld(float delta) {
//        batch.draw(background, 0, 0);
        creature.render(batch);
        gameWorld.render(batch);
    }

    @Override
    protected void renderUi() {
        label.setText(getScore());
        super.renderUi();
    }

    @Override
    public void dispose() {
        gameWorld.dispose();

        super.dispose();
    }

    private void createUI() {
        final Skin uiSkin = assets.get("ui/uiskin.json", Skin.class);

        label = new Label(getScore(), uiSkin);
        uiRootTable.add(label).left();
        uiRootTable.row().expand();
        uiRootTable.add();
    }

    private String getScore() {
        coords.x = Gdx.input.getX();
        coords.y = Gdx.input.getY();
        gameWorld.convertScreenToWorld(coords);
        return StringUtils.format("##, #", LABEL, coords.x, coords.y);
    }
}
