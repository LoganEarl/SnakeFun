package snake;

import processing.core.PApplet;
import processing.event.MouseEvent;
import utils.Point;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class WorldWindow extends PApplet {
    private static final int SCREEN_WIDTH = 1000;
    private static final int SCREEN_HEIGHT = 500;
    private static final double TURN_RATE = 3.0 / 180.0 * Math.PI;

    private Point focusPoint = new Point(15, 10);
    private float focusHeight = 20;
    private float focusWidth = 40;

    private double direction = 0;
    private WorldModel model;


    public void settings() {
        size(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void setup() {
        frameRate(60);
        model = new WorldModel(
                20,
                new Point(100, 100),
                true,
                5,
                0.1, 0.15);
    }

    public void draw() {
        background(64);

        float minFocus = Math.min(focusWidth, focusHeight);
        focusPoint = model.getPlayer().getHead().getPosition();

        Point scale = new Point(Math.min(SCREEN_WIDTH, SCREEN_HEIGHT) / (minFocus * 2), Math.min(SCREEN_WIDTH, SCREEN_HEIGHT) / (minFocus * 2));
        Point bias = new Point();
        Point ll = focusPoint.sub(new Point(focusWidth, focusHeight));
        Point ur = focusPoint.add(new Point(focusWidth, focusHeight));

        Point worldMouse = new Point(mouseX, mouseY);
        worldMouse = worldMouse.sub(bias);
        worldMouse = worldMouse.div(scale);
        worldMouse = worldMouse.add(ll);

        View.drawGrid(this, ll, ur, scale, bias);

        SnakeBody player = model.getPlayer();

        Set<SnakeBody> npcs = model.getLivingSnakes();
        direction += 0.1;
        for (SnakeBody s : npcs) {
            if(!s.equals(player)) //TODO make an npc ai
                s.setHeadDirection(direction);
        }

        List<Food> food =  model.getFoodWithin(ll, ur);
        for(Food f:food){
            View.drawFood(f,this, ll, ur, scale, bias, Color.PINK);
        }

        double curDirection = player.getHeadDirection();
        double targetDirection = focusPoint.angleTo(worldMouse);

        player.setHeadDirection(turnPlayer(curDirection, targetDirection, TURN_RATE));

        model.tick();

        for(SnakeBody s: model.getLivingSnakes()){
            View.drawSnake(s, this, ll, ur, scale, bias);
        }

        model.doSnakeCollisions();
        model.doFoodCollisions();
    }

    @SuppressWarnings("SameParameterValue") //might change it later
    private static double turnPlayer(double curDirection, double targetDirection, double turnRate) {
        double delta1 = (targetDirection - curDirection) % (Math.PI * 2);
        double delta2;
        if(delta1 < 0)
            delta2 = Math.PI * 2 + delta1;
        else
            delta2 = Math.PI * 2 - delta1;

        double selectedDelta;
        selectedDelta = Math.abs(delta1) < Math.abs(delta2) ? delta1 : delta2;
        int sign = selectedDelta >= 0 ? 1 : -1;

        return curDirection + (Math.abs(selectedDelta) > turnRate ? turnRate * sign : selectedDelta);
    }

    private void drawStage(Point ur, Point ll) {
        Point[] boundaries = {new Point()};
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

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[]{WorldWindow.class.getName()};
        PApplet.main(appletArgs);
    }
}
