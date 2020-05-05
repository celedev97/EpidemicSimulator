package com.epidemic_simulator.gui;

import dev.federicocapece.jdaze.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class CameraMove extends GameObject {//TODO: change with GameScript
    public float speed;

    private float zoomSpeed = 4f;

    public CameraMove(float speed) {
        this.speed = speed;
    }

    @Override
    protected void update() {
        cameraMovement();
        cameraZoom();
    }

    private void cameraZoom() {
        if(Input.isKeyDown(KeyEvent.VK_ADD)){
            Engine.camera.zoomIn(1+zoomSpeed*Engine.deltaTime);
        }
        if(Input.isKeyDown(KeyEvent.VK_SUBTRACT)){
            Engine.camera.zoomIn(1-zoomSpeed*Engine.deltaTime);
        }
        float zoom = Input.getMouseWheelRotation();
        Engine.camera.zoomIn(1+zoom*.05f);
    }

    private void cameraMovement() {
        int x = 0;
        int y = 0;

        //left/right
        if(Input.isKeyDown(KeyEvent.VK_LEFT)){
            x--;
        }else if(Input.isKeyDown(KeyEvent.VK_RIGHT)){
            x++;
        }

        //up/down
        if(Input.isKeyDown(KeyEvent.VK_UP)){
            y--;
        }else if(Input.isKeyDown(KeyEvent.VK_DOWN)){
            y++;
        }

        Vector movement = new Vector(x,y);
        Engine.camera.move(movement.multiply(speed * Engine.deltaTime / Engine.camera.getScale()));

    }
}
