package snake;

import processing.core.PApplet;

import java.util.List;

public interface SnakeBody {
    List<Segment> getSegments();
    Segment getHead();
    double getSpeed();
    double getHeadDirection();
    void simulationTick();
    void drawTo(PApplet applet, Point ul, Point lr, double zoom);
}
