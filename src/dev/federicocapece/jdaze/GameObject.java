package dev.federicocapece.jdaze;

import java.awt.*;

public abstract class GameObject {
    public Collider collider = null;

    public Vector position;

    public GameObject() {
        this(Vector.ZERO());
    }

    public GameObject(float x, float y){
        this(new Vector(x, y));
    }

    public GameObject(Vector position){
        this.position = position;
        Engine.gameObjects.add(this);
    }

    protected void update(){}

    protected abstract void draw(Graphics graphics, int x, int y);
}
