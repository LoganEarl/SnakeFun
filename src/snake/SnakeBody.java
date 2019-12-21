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
    double getSegmentSpacing();
    double getFood();
    void addFood(double food);
    void turn(double turnDelta);
    void simulationTick();
    boolean headCollidingWith(Point point, double radius);
    boolean bodyCollidingWith(Point point, double radius);
    void kill();
    boolean isDead();
    //basically, distribute food along each body segment. Used if the snake dies
    double[] getBodyFoodDistribution(double minFood);
    boolean isBoosting();
    void setBoosting(boolean boosting);
    void resurrect(Point position, double startingFood);
}
