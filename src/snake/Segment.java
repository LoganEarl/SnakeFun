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

    public void drawTo(PApplet applet, Point ll, Point ur, Point scale, Point bias, Color rgb){
        double upperX =  position.getX() + radius.get();
        double lowerX = position.getX() - radius.get();
        double upperY = position.getY() + radius.get();
        double lowerY = position.getY() - radius.get();

        //if we are visible on the screen
        if(Utils.linearContains(upperX, lowerX, ur.getX(), ll.getX()) &&
            Utils.linearContains(upperY, lowerY, ur.getY(), ll.getY())){
            Point relativePosition = position.sub(ll);
            applet.fill(rgb.getRed(),rgb.getGreen(), rgb.getBlue());
            applet.ellipse(
                    (float)(relativePosition.getX() * scale.getX() + bias.getX()),
                    (float)(relativePosition.getY() * scale.getY() + bias.getY()),
                    (float)(radius.get() * 2 * scale.getX()),
                    (float)(radius.get() * 2 * scale.getY()));
        }
    }
}
