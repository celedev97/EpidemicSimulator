package dev.federicocapece.jdaze;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;

class Renderer {

    //canvas
    private Canvas canvas;
    private Graphics canvasGraphics;

    //buffered image
    private BufferedImage image;
    private Graphics2D bufferGraphics;

    //manual pixel writings to buffered image
    private int[] pixels;

    public Renderer(Canvas canvas) {

        //canvas init
        canvasGraphics = canvas.getGraphics();

        //buffer init
        image = new BufferedImage(canvas.getWidth(),canvas.getHeight(),BufferedImage.TYPE_INT_RGB);
        bufferGraphics = image.createGraphics();;
        this.image = image;

        //direct pixel edit init:
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        //turn the image full white
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0xffffff;
        }
    }

    public void update(GameObject gameObject) {
        gameObject.draw(bufferGraphics,0,0);
    }

    public void update() {

        canvasGraphics.drawImage(image,0,0,null);
    }

}
