package com.epidemic_simulator.gui.visual;

import dev.federicocapece.jdaze.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * A GameObject that is not drawable.<BR>
 * All it does is moving the camera according to the user input.
 */
public class CameraMove extends GameObject {
    /**
     * The speed of the camera.
     */
    public float speed;


    //#region bounds
    /**
     * A flag using for checking bounds.<BR>
     * If it's set to {@code true} the camera will respect the coordinates bounds that were set at its declaration.<BR>
     * If it's set to {@code false} the camera will be able to move freely in the 2D spaces with no constraints.
     */
    public boolean checkBounds = false;
    private float minX = Float.MIN_VALUE;
    private float minY = Float.MIN_VALUE;
    private float maxX = Float.MAX_VALUE;
    private float maxY = Float.MAX_VALUE;

    /**
     * Sets bound for the camera, and set the {@link #checkBounds} to {@code true}.
     *
     * @see #checkBounds
     *
     * @param minX the min x
     * @param minY the min y
     * @param maxX the max x
     * @param maxY the max y
     */
    public void setBound(float minX, float minY, float maxX, float maxY) {
        checkBounds = true;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }
    //#endregion

    //#region zoom
    private float zoomSpeed = 1f;

    private float minScale = Float.MIN_VALUE;
    private float maxScale = Float.MAX_VALUE;

    /**
     * Sets minimum and maximum scale for the camera zoom.
     *
     * @param minScale the min scale
     * @param maxScale the max scale
     */
    public void setScales(float minScale, float maxScale) {
        this.minScale = minScale;
        this.maxScale = maxScale;
    }
    //#endregion

    /**
     * Instantiates a new Camera move script.
     *
     * @param speed the speed at which the camera will move.
     */
    public CameraMove(float speed) {
        super();
        this.speed = speed;
    }

    /**
     * Sets position for the camera.
     *
     * @param x the x
     * @param y the y
     */
    public void setPosition(float x, float y) {
        Engine.camera.position.set(x, y);
    }

    @Override
    protected void update() {
        cameraMovement();
        cameraZoom();
    }

    private void cameraZoom() {
        if (Input.isKeyDown(KeyEvent.VK_ADD)) {
            Engine.camera.zoomIn(1 + zoomSpeed * Engine.deltaTime);
        }
        if (Input.isKeyDown(KeyEvent.VK_SUBTRACT)) {
            Engine.camera.zoomIn(1 - zoomSpeed * Engine.deltaTime);
        }
        float zoom = Input.getMouseWheelRotation();
        if (zoom != 0) {
            System.out.println(zoom);
        }
        Engine.camera.zoomIn(1 + zoom * .05f);

        //if there's a limit on the scale
        if (minScale != Float.MIN_VALUE) {
            if (Engine.camera.getScale() < minScale) {
                Engine.camera.setScale(minScale);
            } else if (Engine.camera.getScale() > maxScale) {
                Engine.camera.setScale(maxScale);
            }
        }

    }

    private void cameraMovement() {
        Engine.camera.position.sumUpdate(Input.getArrowsVector().multiply(speed * Engine.deltaTime / Engine.camera.getScale()));

        //if there's a limit on the X axys
        if (minX != Float.MIN_VALUE) {
            if (Engine.camera.position.x < minX) {
                Engine.camera.position.x = minX;
            } else if (Engine.camera.position.x > maxX) {
                Engine.camera.position.x = maxX;
            }
            if (Engine.camera.position.y < minY) {
                Engine.camera.position.y = minY;
            } else if (Engine.camera.position.y > maxY) {
                Engine.camera.position.y = maxY;
            }
        }
    }


}
