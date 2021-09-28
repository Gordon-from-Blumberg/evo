package com.gordonfromblumberg.games.core.common.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

import com.gordonfromblumberg.games.core.common.Main;

@SuppressWarnings("rawtypes")
public class GameObject implements Disposable, Pool.Poolable {
    protected static final int X1 = 0;
    protected static final int Y1 = 1;
    protected static final int X2 = 2;
    protected static final int Y2 = 3;
    protected static final int X3 = 4;
    protected static final int Y3 = 5;
    protected static final int X4 = 6;
    protected static final int Y4 = 7;

    protected static int nextId = 1;

    protected int id;

    public GameWorld gameWorld;

    protected final Sprite sprite = new Sprite();
    public final Polygon polygon = createPolygon();
    public final Vector2 position = new Vector2();
    protected float width, height;

    protected final Pool pool;
    protected boolean active = false;
    protected boolean colliding = false;

    protected GameObject() {
        pool = null;
    }

    protected GameObject(Pool pool) {
        this.pool = pool;
    }

    public int getId() {
        return id;
    }

    public void update(float delta) {}

    public void render(Batch batch) {
        final Sprite sprite = this.sprite;
        final Polygon polygon = this.polygon;
        final float width = this.width;
        final float height = this.height;
        sprite.setRotation(polygon.getRotation());
        sprite.setBounds(polygon.getX() - width / 2, polygon.getY() - height / 2, width, height);
        sprite.setOriginCenter();
        sprite.draw(batch);
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    protected Polygon createPolygon() {
        return new Polygon(new float[8]);
    }

    public Rectangle getBoundingRectangle() {
        return polygon.getBoundingRectangle();
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        polygon.setPosition(x, y);
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;

        updateVertices();
    }

    public void setRotation(float deg) {
        polygon.setRotation(deg);
    }

    protected void updateVertices() {
        final float width = this.width;
        final float height = this.height;

        // for correct work of Intersector vertices should be clockwise ordered
        final float[] vertices = polygon.getVertices();
        vertices[X1] = - width / 2;
        vertices[Y1] = - height / 2;
        vertices[X2] = - width / 2;
        vertices[Y2] = height / 2;
        vertices[X3] = width / 2;
        vertices[Y3] = height / 2;
        vertices[X4] = width / 2;
        vertices[Y4] = - height / 2;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    protected static TextureAtlas getTextureAtlas() {
        return Main.getInstance().assets().get("image/texture_pack.atlas", TextureAtlas.class);
    }

    public void setRegion(TextureRegion region) {
        sprite.setRegion(region);
    }

    public void setRegion(String name) {
        sprite.setRegion(getTextureAtlas().findRegion(name));
    }

    public void collide(GameObject other) {
    }

    @SuppressWarnings("unchecked")
    public void free() {
        if (pool != null)
            pool.free(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id;
    }

    @Override
    public void reset() {
        id = -1;
        setPosition(0, 0);
        polygon.setRotation(0);
    }

    @Override
    public void dispose() {
    }
}
