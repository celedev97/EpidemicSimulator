package com.epidemic_simulator.gui;

import dev.federicocapece.jdaze.Engine;
import dev.federicocapece.jdaze.GameObject;
import dev.federicocapece.jdaze.Vector;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CameraMove extends GameObject {//TODO: change with GameScript
    public float speed;

    public CameraMove(float speed) {
        this.speed = speed;
    }

    @Override
    protected void update() {
        Vector movement = Vector.ZERO();
        if(Engine.input.isKeyDown(KeyEvent.VK_LEFT))    movement.sumUpdate(Vector.LEFT());
        if(Engine.input.isKeyDown(KeyEvent.VK_RIGHT))   movement.sumUpdate(Vector.RIGHT());
        if(Engine.input.isKeyDown(KeyEvent.VK_UP))      movement.sumUpdate(Vector.UP());
        if(Engine.input.isKeyDown(KeyEvent.VK_DOWN))    movement.sumUpdate(Vector.DOWN());
        System.out.println("MOVE: " + movement);
        Engine.camera.move(movement.multiply(speed * Engine.deltaTime));
        System.out.println("POS: " + Engine.camera.getPosition() + "\n");
    }

    @Override
    protected void draw(Graphics graphics, int x, int y, float scale) {

    }
}
