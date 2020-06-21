package com.epidemic_simulator.gui.visual;

import dev.federicocapece.jdaze.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class CameraMove extends GameObject {
    public float speed;

    //#region bounds
    public boolean checkBounds = false;
    private float minX = Float.MIN_VALUE;
    private float minY = Float.MIN_VALUE;
    private float maxX = Float.MAX_VALUE;
    private float maxY = Float.MAX_VALUE;

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

    public void setScales(float minScale, float maxScale) {
        this.minScale = minScale;
        this.maxScale = maxScale;
    }
    //#endregion

    public CameraMove(float speed) {
        super();
        this.speed = speed;
    }

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
