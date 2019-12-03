package snake;

import processing.core.PApplet;

import java.awt.*;

public class SnakeWorld extends PApplet {
    private Segment testSeg = new Segment(10,10,2.0);
    private static final double SCREEN_WIDTH = 500;
    private static final double SCREEN_HEIGHT = 500;

    private Point focusPoint = new Point(10,10);
    private float focusWidth = 5;
    private float focusHeight = 5;

    public void settings() {
        size(500, 500);
    }

    public void draw(){
        background(64);

        testSeg.drawTo(this,
                focusPoint.sub(new Point(focusWidth, focusHeight)),
                focusPoint.add(new Point(focusWidth, focusHeight)),
                new Point(SCREEN_HEIGHT/(focusWidth* 2),SCREEN_WIDTH/(focusWidth*2)),
                focusPoint,
                Color.green);

    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "snake.SnakeWorld" };
        PApplet.main(appletArgs);
    }
}
