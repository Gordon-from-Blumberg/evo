package com.gordonfromblumberg.games.core.common.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.gordonfromblumberg.games.core.common.Main;
import com.gordonfromblumberg.games.core.common.factory.AbstractFactory;
import com.gordonfromblumberg.games.core.common.world.GameWorld;
import com.gordonfromblumberg.games.core.common.ui.ButtonConfig;
import com.gordonfromblumberg.games.core.common.ui.FloatField;
import com.gordonfromblumberg.games.core.common.ui.IntField;
import com.gordonfromblumberg.games.core.common.utils.ConfigManager;
import com.gordonfromblumberg.games.core.common.ui.UIUtils;
import com.gordonfromblumberg.games.core.common.world.GameWorldRenderer;
import com.gordonfromblumberg.games.core.evo.event.NewGenerationEvent;

public class GameScreen extends AbstractScreen {
    TextureRegion background;
    private final GameWorld gameWorld;
    private final GameWorldRenderer renderer;

    private final Color pauseColor = Color.LIGHT_GRAY;

    protected GameScreen(SpriteBatch batch) {
        super(batch);

        color = Color.GRAY;
        gameWorld = new GameWorld();
        renderer = new GameWorldRenderer(gameWorld);
    }

    @Override
    public void initialize() {
        super.initialize();

        background = Main.getInstance().assets()
                .get("image/texture_pack.atlas", TextureAtlas.class)
                .findRegion("background");

        final ConfigManager configManager = AbstractFactory.getInstance().configManager();
        gameWorld.initialize(configManager.getInteger("game.world.width"), configManager.getInteger("game.world.height"));
        renderer.initialize(viewport, viewport.getWorldHeight(), viewport.getWorldHeight());
//        gameWorld.newGeneration();
    }

    @Override
    protected void createWorldViewport() {
        final ConfigManager configManager = AbstractFactory.getInstance().configManager();
        final float creatureSize = configManager.getFloat("game.creature.size");
        final float worldHeight = configManager.getInteger("game.world.height") * creatureSize;
        final float minRatio = configManager.getFloat("minRatio");
        final float maxRatio = configManager.getFloat("maxRatio");
        final float minWorldWidth = worldHeight * minRatio;
        final float maxWorldWidth = worldHeight * maxRatio;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        viewport = new ExtendViewport(minWorldWidth, worldHeight, maxWorldWidth, worldHeight, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    protected void update(float delta) {
        super.update(delta);            // apply camera moving and update batch projection matrix
        gameWorld.update(delta);        // update game state
    }

    @Override
    public void dispose() {
        gameWorld.dispose();

        super.dispose();
    }

    @Override
    protected void createUI() {
        super.createUI();

        final ConfigManager config = AbstractFactory.getInstance().configManager();

        // create two main columns: left over game world and right for the UI itself
        uiRootTable.add().width(viewport.getScreenHeight());
        uiRootTable.add().width(viewport.getScreenWidth() - viewport.getScreenHeight());
        uiRootTable.row();

        final Skin uiSkin = assets.get("ui/uiskin.json", Skin.class);

        // over game UI
        final Image pauseImage = new Image(assets.get("image/texture_pack.atlas", TextureAtlas.class).findRegion("pause"));
        pauseImage.setColor(pauseColor);
        pauseImage.setVisible(false);
        uiRootTable.add(pauseImage).align(Align.center);

        // right column UI
        Table uiTable = new Table();
        if (Main.DEBUG_UI)
            uiTable.debugAll();
        uiRootTable.add(uiTable);

        Label generationLbl = new Label("", uiSkin);
        uiTable.add(generationLbl).colspan(5);

        // INFO
        uiTable.row();
        Label creaturesLbl = new Label("", uiSkin);
        uiTable.add(new Label("Creatures\ntotal / birthed", uiSkin)).colspan(2);
        uiTable.add(creaturesLbl);

        uiTable.row();
        Label foodLbl = new Label("", uiSkin);
        uiTable.add(new Label("Food", uiSkin)).colspan(2);
        uiTable.add(foodLbl);

        gameWorld.registerHandler("NewGeneration", e -> {
            NewGenerationEvent event = (NewGenerationEvent) e;
            generationLbl.setText("Generation #" + event.getGenerationNumber());
            creaturesLbl.setText(event.getCreatureCount() + " / " + event.getBirthedCount());
            foodLbl.setText(String.valueOf(event.getFoodCount()));
            return false;
        });

        // FOOD
        uiTable.row().padTop(10);
        uiTable.add(new Label("FOOD", uiSkin)).colspan(5);

        uiTable.row();
        uiTable.add(new Label("Count", uiSkin));
        uiTable.add(new Label("from:", uiSkin)).padLeft(15).padRight(5).align(Align.right);
        IntField.IntFieldBuilder intFieldBuilder = IntField.builder()
                .skin(uiSkin)
                .minValue(1)
                .maxValue(1000);
        uiTable.add(intFieldBuilder.text(config.getString("game.world.food.count.from"))
                .handler(gameWorld.params::setFoodCountFrom)
                .build()
        ).width(50).align(Align.left);
        uiTable.add(new Label("to:", uiSkin)).padLeft(15).padRight(5);
        uiTable.add(intFieldBuilder.text(config.getString("game.world.food.count.to"))
                .handler(gameWorld.params::setFoodCountTo)
                .build()
        ).width(50).align(Align.left);

        uiTable.row().padTop(2);
        uiTable.add(new Label("Value", uiSkin));
        uiTable.add(new Label("from:", uiSkin)).padLeft(15).padRight(5).align(Align.right);
        FloatField.FloatFieldBuilder floatFieldBuilder = FloatField.builder()
                .skin(uiSkin)
                .minValue(1)
                .maxValue(50);
        uiTable.add(floatFieldBuilder.text(config.getString("game.world.food.value.from"))
                .handler(gameWorld.params::setFoodValueFrom)
                .build()
        ).width(50).align(Align.left);
        uiTable.add(new Label("to:", uiSkin)).padLeft(15).padRight(5);
        uiTable.add(floatFieldBuilder.text(config.getString("game.world.food.value.to"))
                .handler(gameWorld.params::setFoodValueTo)
                .build()
        ).width(50).align(Align.left);

        // CREATURES
        uiTable.row().padTop(10);
        uiTable.add(new Label("CREATURES", uiSkin)).colspan(5);

        uiTable.row();
        uiTable.add(new Label("Count of the first generation", uiSkin)).colspan(2).padRight(5);
        uiTable.add(intFieldBuilder.text(config.getString("game.world.creatures.count"))
                .maxValue(10)
                .handler(gameWorld.params::setCreaturesCount)
                .build()
        ).width(50).align(Align.left);

        // GENERATE
        uiTable.row().padTop(20);
        uiTable.add(UIUtils.textButton("Generate",
                uiSkin,
                gameWorld::newGeneration,
                ButtonConfig.toggleDisable(gameWorld, "SimulationStarted", "SimulationFinished")
        )).colspan(2).width(200);

        // SIMULATION
        uiTable.row().padTop(20);
        uiTable.add(new Label("SIMULATION", uiSkin)).colspan(5);
        // ONE GENERATION
        uiTable.row().padTop(2);
        uiTable.add(UIUtils.textButton("One generation",
                uiSkin,
                () -> gameWorld.simulate(1),
                ButtonConfig.toggleDisable(gameWorld, "SimulationStarted", "SimulationFinished")
        )).colspan(2).width(200);

        // SIMULATE N GENERATIONS
        uiTable.row().padTop(2);
        final String generationCount = config.getString("game.world.generation.count");
        final TextButton simulateSomeGensBtn = UIUtils.textButton("Simulate " + generationCount + " generations",
                uiSkin,
                () -> gameWorld.simulate(gameWorld.params.getGenerationCount()),
                ButtonConfig.toggleDisable(gameWorld, "SimulationStarted", "SimulationFinished")
        );
        uiTable.add(simulateSomeGensBtn).colspan(2).width(200);
        uiTable.add(intFieldBuilder.text(generationCount)
                .maxValue(100)
                .handler(n -> {
                    gameWorld.params.setGenerationCount(n);
                    simulateSomeGensBtn.setText("Simulate " + n + " generations");
                })
                .build()
        ).width(50);

        // FREE SIMULATION
        uiTable.row().padTop(2);
        uiTable.add(UIUtils.textButton("Free simulation",
                uiSkin,
                () -> gameWorld.simulate(Integer.MAX_VALUE),
                ButtonConfig.toggleDisable(gameWorld, "SimulationStarted", "SimulationFinished")
        )).colspan(2).width(200);

        // PAUSE AND STOP
        uiTable.row().padTop(15);
        uiTable.add(UIUtils.textButton("Stop",
                uiSkin,
                gameWorld::requestStop,
                ButtonConfig.toggleDisable(gameWorld, "SimulationFinished", "SimulationStarted")
        )).width(90);

        uiTable.add(UIUtils.textButton("Pause",
                uiSkin,
                () -> pauseImage.setVisible(gameWorld.pause()),
                null
        )).width(90);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    pauseImage.setVisible(gameWorld.pause());
                    return true;
                }
                return false;
            }
        });
    }
}
