package dev.federicocapece.jdaze;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MiniGameEngine extends Canvas {
    private Thread runningThread = null;

    private Renderer renderer;

    public ArrayList<GameObject> gameObjects;

    private static MiniGameEngine engine;

    public static MiniGameEngine getEngine() {
        if(engine == null) engine = new MiniGameEngine();
        return engine;
    }

    private MiniGameEngine(){

    }

    public void start(){
        //if a Game thread is already running i try to close it, i cannot start a new one otherwise
        if(runningThread != null) {
            runningThread.interrupt();
            while (runningThread.isAlive()) {
                try {
                    runningThread.interrupt();
                    runningThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //initialize runtime stuff
        renderer = new Renderer(this);
        gameObjects = new ArrayList<>();

        //start the GameLoop
        runningThread = new Thread(){
            @Override
            public void run() {
                while (!this.isInterrupted()){
                    update();
                }
            }
        };
        runningThread.start();
    }

    public void update(){
        for (GameObject gameObject : gameObjects){
            gameObject.update();
            renderer.update(gameObject);
        }

        renderer.update();
    }

}
