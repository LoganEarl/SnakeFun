package snake;

import processing.core.PApplet;
import utils.Utils;

import java.awt.*;

public class Segment {
    private Point position;
    //this is intentionally a reference to the Snake body size. No updates needed
    private Double radius;

    public Segment(Point position, Double radius) {
        this.position = position;
        this.radius = radius;
    }

    public Segment(double x, double y, Double radius) {
        this.position = new Point(x, y);
        this.radius = radius;
    }

    public Point getPosition() {
        return position;
    }

    public Double getRadius() {
        return radius;
    }

    public void drawTo(PApplet applet, Point ll, Point ur, Point scale, Point bias, Color rgb){
        double upperX =  position.getX() + radius;
        double lowerX = position.getX() - radius;
        double upperY = position.getY() + radius;
        double lowerY = position.getY() - radius;

        //if we are visible on the screen
        if(Utils.linearContains(upperX, lowerX, ur.getX(), ll.getX()) &&
            Utils.linearContains(upperY, lowerY, ur.getY(), ll.getY())){
            Point relativePosition = position.sub(ll);
            applet.fill(rgb.getRed(),rgb.getGreen(), rgb.getBlue());
            applet.ellipse(
                    (float)(relativePosition.getX() * scale.getX() + bias.getX()),
                    (float)(relativePosition.getY() * scale.getY() + bias.getY()),
                    (float)(radius * scale.getX()),
                    (float)(radius * scale.getY()));
        }
    }
}
