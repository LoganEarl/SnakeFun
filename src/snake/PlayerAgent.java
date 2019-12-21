package snake;

import utils.Point;

public class PlayerAgent implements SnakeAgent {
    private SnakeBody snake;
    private Point targetPoint = new Point();
    private boolean shouldBoost = false;

    public PlayerAgent() {
    }

    @Override
    public void bindSnake(SnakeBody snake) {
        this.snake = snake;
    }

    @Override
    public void loadInputs(double[] inputs) {
        targetPoint = new Point(inputs[0], inputs[1]);
        shouldBoost = inputs[2] > 0.5;
    }

    @Override
    public void processInputs() {
        //do nothing, dont care
    }

    @Override
    public void setScore(double score) {
        //dont care
    }

    @Override
    public SnakeBody getBoundSnake() {
        return snake;
    }

    @Override
    public void takeActions() {
        double curDirection = snake.getHeadDirection();
        double targetDirection = snake.getHead().getPosition().angleTo(targetPoint);
        double delta = turnPlayer(curDirection, targetDirection);
        snake.turn(delta);
        snake.setBoosting(shouldBoost);
    }

    @SuppressWarnings("SameParameterValue") //might change it later
    private static double turnPlayer(double curDirection, double targetDirection) {
        double rawDiff = curDirection > targetDirection ? curDirection - targetDirection : targetDirection - curDirection;
        double modDiff = rawDiff % (Math.PI * 2);
        double directionChange;

        if (modDiff > Math.PI) {
            directionChange = Math.PI * 2 - modDiff;
            if (targetDirection > curDirection) directionChange = directionChange * -1;
        } else {
            directionChange = modDiff;
            if (curDirection > targetDirection) directionChange = directionChange * -1;
        }

        return directionChange;
    }

    @Override
    public double getScore() {
        return 0;
    }
}
