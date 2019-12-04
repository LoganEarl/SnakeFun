package snake;

import processing.core.PApplet;
import utils.MutableDouble;
import utils.Point;
import utils.Utils;

import java.awt.*;

public class Segment {
    private Point position;
    //this is intentionally a reference to the Snake body size. No updates needed
    private MutableDouble radius;
    private double direction; //in radians

    public Segment(Point position, MutableDouble radius, double direction) {
        this.position = new Point(position);
        this.radius = radius;
        this.direction = direction;
    }

    public Segment(double x, double y, MutableDouble radius, double direction) {
        this.position = new Point(x, y);
        this.radius = radius;
        this.direction = direction;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getRadius() {
        return radius.get();
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }
}
