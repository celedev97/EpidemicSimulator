package com.epidemic_simulator.gui;

import dev.federicocapece.jdaze.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class CameraMove extends GameObject {//TODO: change with GameScript
    public float speed;

    private float zoomSpeed = 4f;

    private float[] cameraBoundX = null;
    private float[] cameraBoundY = null;

    public CameraMove(float speed) {
        super();
        this.speed = speed;
    }

    public static void setPosition(float x, float y) {
        Engine.camera.position.set(x, y);
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
        if(zoom != 0){
            System.out.println(zoom);
        }
        Engine.camera.zoomIn(1+zoom*.05f);
    }

    private void cameraMovement() {
        int x = 0;
        int y = 0;

        //left/right
        if(Input.isKeyDown(KeyEvent.VK_LEFT) || Input.isKeyDown(KeyEvent.VK_A)){
            x--;
        }else if(Input.isKeyDown(KeyEvent.VK_RIGHT) || Input.isKeyDown(KeyEvent.VK_D)){
            x++;
        }

        //up/down
        if(Input.isKeyDown(KeyEvent.VK_UP) || Input.isKeyDown(KeyEvent.VK_W)){
            y--;
        }else if(Input.isKeyDown(KeyEvent.VK_DOWN) || Input.isKeyDown(KeyEvent.VK_S)){
            y++;
        }

        Vector movement = new Vector(x,y);
        Engine.camera.position.sumUpdate(movement.multiply(speed * Engine.deltaTime / Engine.camera.getScale()));

        if(cameraBoundX != null){
            if(Engine.camera.position.x<cameraBoundX[0]){
                Engine.camera.position.x = cameraBoundX[0];
            }else if(Engine.camera.position.x>cameraBoundX[1]){
                Engine.camera.position.x = cameraBoundX[1];
            }
            if(Engine.camera.position.y<cameraBoundY[0]){
                Engine.camera.position.y = cameraBoundY[0];
            }else if(Engine.camera.position.y>cameraBoundY[1]){
                Engine.camera.position.y = cameraBoundY[1];
            }
        }
    }
}
