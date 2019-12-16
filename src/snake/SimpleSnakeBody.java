package snake;

import utils.MutableDouble;
import utils.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleSnakeBody implements SnakeBody {
    private double direction; //in radians
    private MutableDouble segmentRadius;

    private boolean dead = false;

    private double speed;
    private boolean boosting = false;
    private double foodCount;

    private static final double BASE_FOOD_USAGE = 0.0001;
    private static final double BOOST_FOOD_COEFFICIENT = 0.001;
    private static final double MIN_SPEED = 0.1;
    private static final double MAX_SPEED = 0.2;
    private static final double BOOST_SPEED = 0.3;
    private static final double SPEED_COEFFICIENT = 0.0001;
    private static final double BODY_LENGTH_COEFFICIENT = 2.5;
    private static final double BODY_RADIUS_COEFFICIENT = 0.03;
    private List<Segment> bodySegments;

    private Color color;

    public SimpleSnakeBody(int numSegments, Point startPosition, Color color){
        segmentRadius = new MutableDouble(1);
        foodCount = (numSegments - 1)/BODY_LENGTH_COEFFICIENT + 1/BODY_LENGTH_COEFFICIENT - 0.00001;
        bodySegments = new ArrayList<>();
        bodySegments.add(new Segment(startPosition, segmentRadius, 0));

        calculateBody();

        this.color = color;
    }

    private void calculateBody(){
        int targetSegments = calculateTargetSegments(foodCount);
        hitTargetSegments(bodySegments, targetSegments, segmentRadius);
        if(boosting)
            speed = BOOST_SPEED;
        else
            speed = MAX_SPEED - targetSegments * SPEED_COEFFICIENT;

        if(speed < MIN_SPEED)
            speed = MIN_SPEED;
        segmentRadius.set(1+ targetSegments * BODY_RADIUS_COEFFICIENT);
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
    public boolean bodyCollidingWith(Point point, double radius) {
        //no need to check each segment if we are too far away to possibly be colliding
        if(bodySegments.size() * segmentRadius.get() > radius + point.distanceTo(bodySegments.get(0).getPosition())){
            double size = segmentRadius.get();
            for(Segment segment: bodySegments)
                if(segment.getPosition().distanceTo(point) <= size + radius)
                    return true;
        }
        return false;
    }

    @Override
    public boolean headCollidingWith(Point point, double radius) {
        return point.distanceTo(bodySegments.get(0).getPosition()) + radius <= segmentRadius.get();
    }

    @Override
    public void kill() {
        dead = true;
    }

    @Override
    public void simulationTick() {
        if(!dead) {
            if (boosting)
                foodCount -= BASE_FOOD_USAGE + BOOST_FOOD_COEFFICIENT * calculateTargetSegments(foodCount);
            else
                foodCount -= BASE_FOOD_USAGE;

            calculateBody();

            assert bodySegments.size() > 0;
            Point lastPos = bodySegments.get(0).getPosition();
            lastPos = lastPos.go(direction, speed);
            bodySegments.get(0).setPosition(lastPos);
            for (int i = 1; i < bodySegments.size(); i++) {
                Point curPos = bodySegments.get(i).getPosition();
                double distance = curPos.distanceTo(lastPos);
                if (distance > segmentRadius.get()) {
                    double angle = curPos.angleTo(lastPos);
                    curPos.set(curPos.go(angle, distance - segmentRadius.get()));
                }
                lastPos = curPos;
            }
        }
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
    public Color getColor() {
        return color;
    }

    @Override
    public double getFood() {
        return foodCount;
    }

    @Override
    public void addFood(double food) {
        foodCount += food;
    }

    @Override
    public boolean isDead() {
        return dead;
    }
}
