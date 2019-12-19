package snake;

import processing.core.PApplet;
import processing.core.PFont;
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

    private PFont font;

    public void settings() {
        size(SCREEN_WIDTH, SCREEN_HEIGHT, P3D);
    }

    public void setup() {
        frameRate(60);
        model = new WorldModel(
                10,
                new Point(100, 100),
                true,
                2,
                0.1, 0.3);
        smooth();
        font = createFont("Arial Bold",48);
    }

    public void draw() {
        hint(DISABLE_DEPTH_MASK);
        hint(DISABLE_DEPTH_TEST);
        //hint(DISABLE_OPTIMIZED_STROKE);

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
            if (!s.equals(player)) //TODO make an npc ai
                s.setHeadDirection(direction);
        }

        List<Food> food = model.getFoodWithin(ll, ur);
        for (Food f : food) {
            View.drawFood(f, this, ll, ur, scale, bias, Color.PINK);
        }

        double curDirection = player.getHeadDirection();
        double targetDirection = focusPoint.angleTo(worldMouse);

        player.setHeadDirection(turnPlayer(curDirection, targetDirection, TURN_RATE));

        model.tick();

        for (SnakeBody s : model.getLivingSnakes()) {
            View.drawSnake(s, this, ll, ur, scale, bias);
        }

        model.doSnakeCollisions();
        model.doFoodCollisions();

        textFont(font,36);
        // white float frameRate
        fill(255);
        text(frameRate,20,20);
        // gray int frameRate display:
        fill(200);
        text((int)(frameRate),20,60);
    }

    @SuppressWarnings("SameParameterValue") //might change it later
    private static double turnPlayer(double curDirection, double targetDirection, double turnRate) {
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

        int sign = directionChange >= 0 ? 1 : -1;

        return curDirection + (Math.abs(directionChange) > turnRate ? turnRate * sign : directionChange);
    }

    private static double toDegrees(double d) {
        return d * 180 / Math.PI;
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
        model.getPlayer().setBoosting(true);
    }

    @Override
    public void mouseReleased() {
        model.getPlayer().setBoosting(false);
    }

    public static void main(String[] passedArgs) {
        String[] appletArgs = new String[]{WorldWindow.class.getName()};
        PApplet.main(appletArgs);
    }
}
