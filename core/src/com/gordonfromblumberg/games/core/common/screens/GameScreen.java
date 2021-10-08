package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.model.GameWorld;
import com.gordonfromblumberg.games.core.common.ui.FloatField;
import com.gordonfromblumberg.games.core.common.ui.IntField;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;
import com.gordonfromblumberg.games.core.evo.event.NewGenerationEvent;

public class GameScreen extends AbstractScreen {
    TextureRegion background;
    private GameWorld gameWorld;

    private final Vector3 coords = new Vector3();

    private Label pauseText;

    protected GameScreen(SpriteBatch batch) {
        super(batch);

        color = Color.GRAY;
        gameWorld = new GameWorld();
    }

    @Override
    public void initialize() {
        super.initialize();

        background = Main.getInstance().assets()
                .get("image/texture_pack.atlas", TextureAtlas.class)
                .findRegion("background");

        gameWorld.initialize(viewport.getWorldHeight());
        gameWorld.newGeneration();

        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Target", "Target = " + x + ", " + y);
                gameWorld.herb.setTarget(x, y);
            }
        });

//        pauseText = new BitmapFontCache(assets.get("ui/uiskin.json", Skin.class).getFont("default-font"));
//        pauseText.setText("PAUSE", 100, 100);
    }

    @Override
    protected void createWorldViewport() {
        final ConfigManager configManager = AbstractFactory.getInstance().configManager();
        Main.CREATURE_SIZE = configManager.getFloat("game.creature.size");
        final float worldWidth = configManager.getFloat("game.size") * Main.CREATURE_SIZE;
        final float minRatio = configManager.getFloat("minRatio");
        final float maxRatio = configManager.getFloat("maxRatio");
        final float minWorldHeight = worldWidth / maxRatio;
        final float maxWorldHeight = worldWidth / minRatio;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        viewport = new ExtendViewport(worldWidth, minWorldHeight, worldWidth, maxWorldHeight, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        uiViewport.update(width, height, true);
    }

    @Override
    protected void update(float delta) {
        super.update(delta);            // apply camera moving and update batch projection matrix
        gameWorld.update(delta);        // update game state
    }

    @Override
    protected void renderWorld(float delta) {
        gameWorld.render(batch);
    }

    @Override
    protected void renderUi() {
        super.renderUi();
    }

    @Override
    public void dispose() {
        gameWorld.dispose();

        super.dispose();
    }

    @Override
    protected void createUI() {
        super.createUI();

        final Skin uiSkin = assets.get("ui/uiskin.json", Skin.class);

//        uiRootTable.setFillParent(false);
        pauseText = new Label("PAUSE", uiSkin);
        pauseText.setColor(Color.GRAY);
        pauseText.setFontScale(4);
        gameWorld.registerHandler("", e -> {
            // TODO
            return false;
        });
        uiRootTable.add(pauseText).align(Align.center);
        Table uiTable = new Table();
//        uiTable.setFillParent(true);
        uiRootTable.add(uiTable).width(viewport.getScreenWidth() - viewport.getScreenHeight());
        uiRootTable.row();
        uiRootTable.add().width(viewport.getScreenHeight());
//        uiRootTable.setWidth();
        Gdx.app.log("UI", "uiRootTable width = " + (viewport.getScreenWidth() - viewport.getScreenHeight()));
//        uiTable.setX(viewport.getScreenHeight());
        Gdx.app.log("UI", "uiRootTable x = " + viewport.getScreenHeight());
//        uiTable.setY(500);

        Label label = new Label("", uiSkin);
        gameWorld.registerHandler("NewGeneration", e -> {
            label.setText("Generation #" + ((NewGenerationEvent) e).getGenerationNumber());
            return false;
        });
        uiTable.add(label).colspan(5);

        uiTable.row().padTop(10);
        uiTable.add(new Label("FOOD", uiSkin)).colspan(5);

        uiTable.row();
        uiTable.add(new Label("Count", uiSkin));
        uiTable.add(new Label("from:", uiSkin)).padLeft(15);
        IntField.IntFieldBuilder intFieldBuilder = IntField.builder()
                .skin(uiSkin)
                .minValue(1)
                .maxValue(1000);
        uiTable.add(intFieldBuilder.text("10").handler(gameWorld.params::setFoodCountFrom).build()).width(50).align(Align.left);
        uiTable.add(new Label("to:", uiSkin)).padLeft(15);
        uiTable.add(intFieldBuilder.text("10").handler(gameWorld.params::setFoodCountTo).build()).width(50).align(Align.left);

        uiTable.row();
        uiTable.add(new Label("Value", uiSkin));
        uiTable.add(new Label("from:", uiSkin)).padLeft(15);
        FloatField.FloatFieldBuilder floatFieldBuilder = FloatField.builder()
                .skin(uiSkin)
                .minValue(1)
                .maxValue(50);
        uiTable.add(floatFieldBuilder.text("10").handler(gameWorld.params::setFoodValueFrom).build()).width(50).align(Align.left);
        uiTable.add(new Label("to:", uiSkin)).padLeft(15);
        uiTable.add(floatFieldBuilder.text("10").handler(gameWorld.params::setFoodValueTo).build()).width(50).align(Align.left);

        TextButton generateBtn = new TextButton("Generate", uiSkin);
        generateBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameWorld.newGeneration();
            }
        });
        uiTable.row();
        uiTable.add(generateBtn).colspan(5).padTop(20);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    gameWorld.pause();
                    return true;
                }
                return false;
            }
        });
    }
}
