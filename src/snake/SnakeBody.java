package snake;

import processing.core.PApplet;
import utils.Point;

import java.awt.*;
import java.util.List;

public interface SnakeBody {
    List<Segment> getSegments();
    Segment getHead();
    double getSpeed();
    double getHeadDirection();
    Color getColor();
    double getFood();
    void addFood(double food);
    void setHeadDirection(double direction);
    void simulationTick();
    boolean headCollidingWith(Point point, double radius);
    boolean bodyCollidingWith(Point point, double radius);
    void kill();
    boolean isDead();
}
