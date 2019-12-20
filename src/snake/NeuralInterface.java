package snake;

import utils.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeuralInterface {
    public static double[] generateInputs(SnakeBody snake, double viewDistance, WorldModel model){
        double currentDirection = snake.getHeadDirection();
        Point currentPosition = snake.getHead().getPosition();

        List<Food> nearbyFood = model.getFoodNear(currentPosition, viewDistance);
        List<SnakeBody> allSnakes = new ArrayList<>(model.getLivingSnakes());
        List<Segment> nearbySegments = new ArrayList<>();

        for(SnakeBody nearbySnake: allSnakes){
            if(!nearbySnake.equals(snake)){
                //no need to check each segment if we are too far away to possibly be colliding
                List<Segment> segments = nearbySnake.getSegments();
                double maxSnakeLength = segments.size() * nearbySnake.getHead().getRadius() * nearbySnake.getSegmentSpacing();
                if(maxSnakeLength > viewDistance + currentPosition.distanceTo(nearbySnake.getHead().getPosition())){
                    for(Segment segment:segments)
                        if(currentPosition.distanceTo(currentPosition) < viewDistance)
                            nearbySegments.add(segment);
                }
            }
        }

        double[] inputs = new double[19];

        inputs[18] = 1.0/(snake.getFood() + 1);

        for(Segment segment: nearbySegments){
            int inputIndex = inputLocationOfItem(segment.getPosition(), currentPosition, currentDirection);
            if(inputIndex != -1){
                double currentActivation = inputs[inputIndex];
                double distance = currentPosition.distanceTo(segment.getPosition()) - segment.getRadius() - snake.getHead().getRadius();
                double proposedActivation = (viewDistance - distance)/viewDistance;
                inputs[inputIndex] = Math.max(currentActivation, proposedActivation);
            }
        }

        for(Food food: nearbyFood){
            int inputIndex = inputLocationOfItem(food.getPosition(), currentPosition, currentDirection);
            if(inputIndex != -1){
                inputIndex += 9;
                double distance = currentPosition.distanceTo(food.getPosition()) - food.getAmount() - snake.getHead().getRadius();
                double itemActivation = (viewDistance - distance)/viewDistance *
                        food.getAmount()/(model.getMaxFood() - model.getMinFood());
                inputs[inputIndex] += itemActivation;
            }
        }

        return inputs;
    }

    private static int inputLocationOfItem(Point itemPosition, Point currentPosition, double currentDirection){
        double angle = currentPosition.angleTo(itemPosition) + currentDirection;
        while(angle < Math.PI) angle += 2 * Math.PI;
        while(angle > Math.PI) angle -= 2 * Math.PI;

        if(angle >= Math.PI/-1.0 && angle < Math.PI/-2.0)
            return 0;
        if(angle >= Math.PI/-2.0 && angle < Math.PI/-3.0)
            return 1;
        if(angle >= Math.PI/-3.0 && angle < Math.PI/-6.0)
            return 2;
        if(angle >= Math.PI/-6.0 && angle < Math.PI/-18.0)
            return 3;
        if(angle >= Math.PI/-18.0 && angle <= Math.PI/18.0)
            return 4;
        if(angle <= Math.PI/1.0 && angle > Math.PI/2.0)
            return 5;
        if(angle <= Math.PI/2.0 && angle > Math.PI/3.0)
            return 6;
        if(angle <= Math.PI/3.0 && angle > Math.PI/6.0)
            return 7;
        if(angle <= Math.PI/6.0 && angle > Math.PI/18.0)
            return 8;
        return -1;
    }
}
