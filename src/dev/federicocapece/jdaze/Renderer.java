package dev.federicocapece.jdaze;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

class Renderer extends Canvas {

    //canvas
    private Graphics canvasGraphics;

    //buffered image
    private BufferedImage image;
    private Graphics2D bufferGraphics;

    //manual pixel writings to buffered image
    private int[] pixels;

    public void init(){
        //canvas init
        canvasGraphics = getGraphics();

        //buffer init
        image = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
        bufferGraphics = image.createGraphics();;
        this.image = image;

        //direct pixel edit init:
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        clean();
    }

    public void update(GameObject gameObject) {
        //TODO: calcoli basati sulla telecamera
        gameObject.draw(bufferGraphics,(int)gameObject.position.x,(int)gameObject.position.y);
    }

    public void update() {
        canvasGraphics.drawImage(image,0,0,null);
    }

    public void clean() {
        //turn the image full white
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0xffffff;
        }
    }
}
