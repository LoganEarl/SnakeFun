package snake;

import processing.core.PApplet;
import utils.MutableDouble;
import utils.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleSnakeBody implements SnakeBody {
    private double direction; //in radians
    private MutableDouble segmentWidth;

    private double speed;
    private boolean boosting = false;
    private double foodCount;

    private static final double BASE_FOOD_USAGE = 0.0001;
    private static final double BOOST_FOOD_COEFFICIENT = 0.001;
    private static final double MIN_SPEED = 0.1;
    private static final double MAX_SPEED = 0.5;
    private static final double BOOST_SPEED = 0.8;
    private static final double SPEED_COEFFICIENT = 0.0001;
    private static final double BODY_LENGTH_COEFFICIENT = 2.5;
    private static final double BODY_RADIUS_COEFFICIENT = 0.03;
    private List<Segment> bodySegments;

    private Color color;

    public SimpleSnakeBody(int numSegments, Point startPosition, Color color){
        segmentWidth = new MutableDouble(1);
        foodCount = (numSegments - 1)/BODY_LENGTH_COEFFICIENT + 1/BODY_LENGTH_COEFFICIENT - 0.00001;
        bodySegments = new ArrayList<>();
        bodySegments.add(new Segment(startPosition, segmentWidth, 0));

        calculateBody();

        this.color = color;
    }

    private void calculateBody(){
        int targetSegments = calculateTargetSegments(foodCount);
        hitTargetSegments(bodySegments, targetSegments, segmentWidth);
        if(boosting)
            speed = BOOST_SPEED;
        else
            speed = MAX_SPEED - targetSegments * SPEED_COEFFICIENT;

        if(speed < MIN_SPEED)
            speed = MIN_SPEED;
        segmentWidth.set(1+ targetSegments * BODY_RADIUS_COEFFICIENT);
    }

    private static int calculateTargetSegments(double foodCount){
        return (int)(foodCount * BODY_LENGTH_COEFFICIENT) + 1;
    }

    private static void hitTargetSegments(List<Segment> currentSegments, int targetSegments, MutableDouble size){
        Point lastSegmentPosition = currentSegments.get(currentSegments.size()-1).getPosition();
        double lastDirection = currentSegments.get(currentSegments.size()-1).getDirection();
        while(currentSegments.size() < targetSegments)
            currentSegments.add(new Segment(lastSegmentPosition, size, lastDirection));
        while(currentSegments.size() > targetSegments)
            currentSegments.remove(currentSegments.size()-1);
    }

    @Override
    public List<Segment> getSegments() {
        return bodySegments;
    }

    @Override
    public Segment getHead() {
        return bodySegments.get(0);
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    //in radians
    @Override
    public double getHeadDirection() {
        return direction;
    }

    //in radians
    @Override
    public void setHeadDirection(double direction){
        this.direction = direction;
    }

    @Override
    public void simulationTick() {
        if(boosting)
            foodCount -= BASE_FOOD_USAGE + BOOST_FOOD_COEFFICIENT * calculateTargetSegments(foodCount);
        else
            foodCount -= BASE_FOOD_USAGE;

        calculateBody();

        assert bodySegments.size() > 0;
        Point lastPos = bodySegments.get(0).getPosition();
        lastPos = lastPos.go(direction, speed);
        bodySegments.get(0).setPosition(lastPos);
        for(int i = 1; i < bodySegments.size(); i++){
            Point curPos = bodySegments.get(i).getPosition();
            double distance = curPos.distanceTo(lastPos);
            if(distance > segmentWidth.get()){
                double angle = curPos.angleTo(lastPos);
                curPos.set(curPos.go(angle, distance - segmentWidth.get()));
            }
            lastPos = curPos;
        }
    }

    @Override
    public void drawTo(PApplet applet, Point ll, Point ur, Point scale, Point bias) {
        for(int i = bodySegments.size()-1; i >= 0; i--)
            bodySegments.get(i).drawTo(applet, ll, ur, scale, bias, color);
    }
}
