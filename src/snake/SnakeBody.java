package snake;

import processing.core.PApplet;
import utils.Point;

import java.util.List;

public interface SnakeBody {
    List<Segment> getSegments();
    Segment getHead();
    double getSpeed();
    double getHeadDirection();
    void setHeadDirection(double direction);
    void simulationTick();
    void drawTo(PApplet applet, Point ll, Point ur, Point scale, Point bias);
}
