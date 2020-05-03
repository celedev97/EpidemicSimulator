package dev.federicocapece.jdaze;

import java.awt.*;

public abstract class GameObject {
    public Collider collider = null;

    public Vector position = Vector.ZERO();

    public GameObject() {
        MiniGameEngine.getEngine();
    }

    protected void update(){}

    protected abstract void draw(Graphics graphics, int x, int y);
}
