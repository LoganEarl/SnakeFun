package snake;

import ai.jneat.Neat;
import ai.jneat.Organism;
import ai.jneat.Population;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;
import utils.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WorldWindow extends PApplet {
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 500;

    private PlayerAgent playerAgent;
    private boolean mouseDown = false;

    private List<LearningAgent> learningAgents;
    private Population population;
    private int generation = 0;

    private Point focusPoint = new Point(15, 10);
    private float focusHeight = 20;
    private float focusWidth = 40;
    private int scrollDirection = -1;
    private static final double SENSOR_DISTANCE = 10;
    private static final boolean ALLOW_PLAYER = false;
    private boolean fastForward = false;

    private WorldModel model;

    private PFont font;

    public WorldWindow() {
        model = new WorldModel(
                ALLOW_PLAYER ? 31 : 30,
                new Point(100, 100),
                2,
                0.1, 0.3);

        if (ALLOW_PLAYER)
            playerAgent = new PlayerAgent();

        Neat.initbase();
        population = new Population(30, 19, 2, 5, true, 0.5);

        learningAgents = new ArrayList<>(60);
        for (Object o : population.getOrganisms()) {
            Organism organism = (Organism) o;   //Not even any generics. ugh
            learningAgents.add(new LearningAgent(organism));
        }

        List<SnakeBody> snakes = List.copyOf(model.getAllSnakes());
        for (int i = 0; i < learningAgents.size(); i++) {
            learningAgents.get(i).bindSnake(snakes.get(i));
        }

        if (ALLOW_PLAYER)
            playerAgent.bindSnake(snakes.get(snakes.size() - 1));
    }

    public void settings() {
        size(SCREEN_WIDTH, SCREEN_HEIGHT, P3D);
        smooth();
    }

    public void setup() {
        frameRate(360);
        font = createFont("Arial Bold", 48);
    }

    public void draw() {
        hint(DISABLE_DEPTH_MASK);
        hint(DISABLE_DEPTH_TEST);
        hint(DISABLE_OPTIMIZED_STROKE);

        switch (scrollDirection) {
            case 0:
                focusPoint.setY(focusPoint.getY() - 1);
                break;
            case 2:
                focusPoint.setY(focusPoint.getY() + 1);
                break;
            case 3:
                focusPoint.setX(focusPoint.getX() - 1);
                break;
            case 1:
                focusPoint.setX(focusPoint.getX() + 1);
                break;
        }

        background(64);

        if(!fastForward) {
            float minFocus = Math.min(focusWidth, focusHeight);

            @SuppressWarnings("ConstantConditions")
            Point scale = new Point(Math.min(SCREEN_WIDTH, SCREEN_HEIGHT) / (minFocus * 2), Math.min(SCREEN_WIDTH, SCREEN_HEIGHT) / (minFocus * 2));
            Point bias = new Point();
            Point ll = focusPoint.sub(new Point(focusWidth, focusHeight));
            Point ur = focusPoint.add(new Point(focusWidth, focusHeight));

            if (ALLOW_PLAYER) {
                Point worldMouse = new Point(mouseX, mouseY);
                worldMouse = worldMouse.sub(bias);
                worldMouse = worldMouse.div(scale);
                worldMouse = worldMouse.add(ll);
                playerAgent.loadInputs(new double[]{worldMouse.getX(), worldMouse.getY(), mouseDown ? 1 : 0});
                playerAgent.takeActions();
                focusPoint.set(playerAgent.getBoundSnake().getHead().getPosition());
            }

            View.drawGrid(this, ll, ur, scale, bias);

            List<Food> food = model.getFoodWithin(ll, ur);
            for (Food f : food)
                View.drawFood(f, this, ll, ur, scale, bias, Color.PINK);

            for (SnakeBody s : model.getLivingSnakes())
                View.drawSnake(s, this, ll, ur, scale, bias);
        }

        for (LearningAgent agent : learningAgents) {
            agent.loadInputs(NeuralInterface.generateInputs(agent.getBoundSnake(), SENSOR_DISTANCE, model));
            agent.processInputs();
            agent.takeActions();
        }

        model.tick();
        model.doSnakeCollisions();
        model.doFoodCollisions();


        View.drawFPS(this, font);
        View.drawGeneration(this, font, generation);
    }

    private void markEpoch() {
        for (LearningAgent agent : learningAgents) {
            double score = agent.getBoundSnake().getFood();
            if (agent.getBoundSnake().isDead())
                score = score * 0.5;

            agent.setScore(score);
        }

        population.print_to_file_by_species("data/Generation:" + generation + ".txt");
        population.epoch(generation++);

        if (ALLOW_PLAYER)
            playerAgent = new PlayerAgent();

        learningAgents.clear();
        for (Object o : population.getOrganisms()) {
            Organism organism = (Organism) o;   //fuck vectors man. This guy really didn't know java
            learningAgents.add(new LearningAgent(organism));
        }

        model = new WorldModel(
                ALLOW_PLAYER ? 31 : 30,
                new Point(100, 100),
                2,
                0.1, 0.3);

        List<SnakeBody> snakes = List.copyOf(model.getAllSnakes());
        for (int i = 0; i < learningAgents.size(); i++)
            learningAgents.get(i).bindSnake(snakes.get(i));
        if (ALLOW_PLAYER)
            playerAgent.bindSnake(snakes.get(snakes.size() - 1));
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        float minFocus = Math.min(focusWidth, focusHeight);
        float e = event.getCount();
        if (minFocus + e > 5 && minFocus + e < 100) {
            focusHeight += e;
            focusWidth += e;
        }
    }

    @Override
    public void mousePressed() {
        mouseDown = true;
    }

    @Override
    public void mouseReleased() {
        mouseDown = false;
    }

    @Override
    public void keyPressed() {
        switch (keyCode) {
            case UP:
                scrollDirection = 0;
                break;
            case DOWN:
                scrollDirection = 2;
                break;
            case LEFT:
                scrollDirection = 3;
                break;
            case RIGHT:
                scrollDirection = 1;
                break;
            case 'g':
            case 'G':
                markEpoch();
                break;
            case 'f':
            case 'F':
                fastForward = !fastForward;
        }
    }

    @Override
    public void keyReleased() {
        if (keyCode == UP || keyCode == DOWN || keyCode == LEFT || keyCode == RIGHT)
            scrollDirection = -1;
    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[]{WorldWindow.class.getName()};
        PApplet.main(appletArgs);
    }
}
