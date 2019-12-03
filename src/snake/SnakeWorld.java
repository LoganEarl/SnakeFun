package snake;

import processing.core.PApplet;
import processing.event.MouseEvent;
import utils.Point;

import java.awt.*;

public class SnakeWorld extends PApplet {
    private SnakeBody body;
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 500;

    private Point focusPoint = new Point(15, 10);
    private float focusHeight = 20;
    private float focusWidth = 40;

    public void settings() {
        size(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void setup() {
        frameRate(30);
        body = new SimpleSnakeBody(20, new Point(), Color.GREEN);
    }

    public void draw() {
        background(64);

        float minFocus = Math.min(focusWidth, focusHeight);
        focusPoint = body.getHead().getPosition();

        Point scale = new Point(Math.min(SCREEN_WIDTH, SCREEN_HEIGHT) / (minFocus * 2), Math.min(SCREEN_WIDTH, SCREEN_HEIGHT) / (minFocus * 2));
        Point bias = new Point();
        Point ll = focusPoint.sub(new Point(focusWidth, focusHeight));
        Point ur = focusPoint.add(new Point(focusWidth, focusHeight));

        Point worldMouse = new Point(mouseX,mouseY);
        //Point worldMouse = new Point(200,200);
        worldMouse = worldMouse.sub(bias);
        worldMouse = worldMouse.div(scale);
        worldMouse = worldMouse.add(ll);

        body.setHeadDirection(focusPoint.angleTo(worldMouse));
        body.simulationTick();
        body.drawTo(this, ll, ur, scale, bias);

    }

    @Override
    public void mouseWheel(MouseEvent event) {
        float minFocus = Math.min(focusWidth, focusHeight);
        float e = event.getCount();
        if (minFocus + e > 5 && minFocus + e < 100) {
            focusHeight += e;
            focusWidth += e;
        }

    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"snake.SnakeWorld"};
        PApplet.main(appletArgs);
    }
}
