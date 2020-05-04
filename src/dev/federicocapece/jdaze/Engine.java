package dev.federicocapece.jdaze;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public final class Engine {
    private static Thread runningThread = null;

    public static Renderer renderer = new Renderer();

    protected static ArrayList<GameObject> gameObjects;

    private static StopWatch stopWatch = new StopWatch();

    public static float deltaTime;


    public static void start(){
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
        renderer.init();
        gameObjects = new ArrayList<>();

        stopWatch.start();

        //start the GameLoop
        runningThread = new Thread(){
            @Override
            public void run() {
                while (!isInterrupted()){
                    update();
                }
            }
        };

        runningThread.start();
    }

    public static void update(){
        stopWatch.start();
        renderer.clean();
        for (GameObject gameObject : gameObjects){
            gameObject.update();
            renderer.update(gameObject);
        }
        renderer.update();

        deltaTime = stopWatch.getElapsedTime() / 1000.0f;
        System.out.println("deltaTime = "+deltaTime);
        System.out.println("fps = "+ (1/deltaTime));
    }

}
