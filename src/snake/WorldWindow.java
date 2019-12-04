package snake;

import processing.core.PApplet;
import processing.event.MouseEvent;
import utils.Point;
import utils.Utils;

import java.awt.*;
import java.util.Set;

public class WorldWindow extends PApplet {
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 500;

    private Point focusPoint = new Point(15, 10);
    private float focusHeight = 20;
    private float focusWidth = 40;

    private double direction = 0;
    private WorldModel model;

    public void settings() {
        size(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void setup() {
        frameRate(60);
        model = new WorldModel(20, new Point(100,100), true);
    }

    public void draw() {
        background(64);

        float minFocus = Math.min(focusWidth, focusHeight);
        focusPoint = model.getPlayer().getHead().getPosition();

        Point scale = new Point(Math.min(SCREEN_WIDTH, SCREEN_HEIGHT) / (minFocus * 2), Math.min(SCREEN_WIDTH, SCREEN_HEIGHT) / (minFocus * 2));
        Point bias = new Point();
        Point ll = focusPoint.sub(new Point(focusWidth, focusHeight));
        Point ur = focusPoint.add(new Point(focusWidth, focusHeight));

        Point worldMouse = new Point(mouseX,mouseY);
        worldMouse = worldMouse.sub(bias);
        worldMouse = worldMouse.div(scale);
        worldMouse = worldMouse.add(ll);

        View.drawGrid(this, ll, ur, scale, bias);

        Set<SnakeBody> npcs = model.getNPCSnakes();
        direction += 0.1;
        for(SnakeBody s: npcs) {
            s.setHeadDirection(direction);
            s.simulationTick();
            View.drawSnake(s,this, ll, ur, scale, bias);
        }

        model.getPlayer().addFood(0.01);
        model.getPlayer().setHeadDirection(focusPoint.angleTo(worldMouse));
        model.getPlayer().simulationTick();
        View.drawSnake(model.getPlayer(),this, ll, ur, scale, bias);
    }

    private void drawStage(Point ur, Point ll){
        Point[] boundaries = {new Point()};
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
        String[] appletArgs = new String[]{WorldWindow.class.getName()};
        PApplet.main(appletArgs);
    }
}
