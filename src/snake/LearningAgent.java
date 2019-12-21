package snake;

import ai.jneat.NNode;
import ai.jneat.Network;
import ai.jneat.Organism;

public class LearningAgent implements SnakeAgent {
    private Organism neuroOrganism;
    private SnakeBody snake;
    private boolean boundSnake = false;

    private double maxScore = 0;

    public LearningAgent(Organism organism){
        this.neuroOrganism = organism;
    }

    @Override
    public void bindSnake(SnakeBody snake) {
        if(boundSnake)
            throw new IllegalArgumentException("Cannot bind more than one snake to an agent");
        this.snake = snake;
        boundSnake = true;
    }

    @Override
    public void loadInputs(double[] inputs) {
        if(inputs.length != NUM_INPUTS)
            throw new IllegalArgumentException("Incorrect number of inputs for neural structure");
        double[] packedInputs = new double[inputs.length + 1];
        System.arraycopy(inputs, 0, packedInputs, 0, inputs.length);
        packedInputs[inputs.length] = -1; //pack in the bias
        neuroOrganism.getNet().load_sensors(packedInputs);
    }

    @Override
    public void processInputs() {
        Network brain = neuroOrganism.getNet();
        int neuroDepth = brain.max_depth();

        brain.activate();
        for(int relax = 0; relax < neuroDepth; relax++)
            brain.activate();
    }

    @Override
    public void takeActions() {
        Network brain = neuroOrganism.getNet();
        double negativeTurnConfidence = ((NNode)brain.getOutputs().elementAt(0)).getActivation();
        double positiveTurnConfidence = ((NNode)brain.getOutputs().elementAt(1)).getActivation();
        double directionChange = positiveTurnConfidence - negativeTurnConfidence;
        double boostConfidence = ((NNode)brain.getOutputs().elementAt(2)).getActivation();

        snake.turn(directionChange);
        snake.setBoosting(boostConfidence > 0.5);
        //snake.setBoosting(false);
    }

    @Override
    public SnakeBody getBoundSnake() {
        return snake;
    }

    @Override
    public void setScore(double score) {
        if(score > maxScore) {
            maxScore = score;
            neuroOrganism.setFitness(maxScore);
        }
    }

    @Override
    public double getScore() {
        return maxScore;
    }
}
