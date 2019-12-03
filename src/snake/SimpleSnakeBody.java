package snake;

import processing.core.PApplet;

import java.awt.*;
import java.util.List;

public class SimpleSnakeBody implements SnakeBody {
    private double direction; //in radians
    private Double size;

    public SimpleSnakeBody(int numSegments, Color color){
        size = 1+ numSegments * 0.01;
    }

    @Override
    public List<Segment> getSegments() {
        return null;
    }

    @Override
    public Segment getHead() {
        return null;
    }

    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public double getHeadDirection() {
        return 0;
    }

    @Override
    public void simulationTick() {

    }

    @Override
    public void drawTo(PApplet applet, Point ul, Point lr, double zoom) {

    }
}
