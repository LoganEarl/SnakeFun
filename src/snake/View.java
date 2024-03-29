package snake;

import processing.core.PApplet;
import processing.core.PFont;
import utils.MutableDouble;
import utils.Point;
import utils.Utils;

import java.awt.*;
import java.util.List;

public class View {
    public static void drawFood(Food food,  PApplet applet, Point ll, Point ur, Point scale, Point bias, Color rgb){
        Point position = food.getPosition();
        double radius = food.getAmount();
        double upperX =  position.getX() + radius;
        double lowerX = position.getX() - radius;
        double upperY = position.getY() + radius;
        double lowerY = position.getY() - radius;

        //applet.noStroke();

        if(Utils.linearContains(upperX, lowerX, ur.getX(), ll.getX()) &&
                Utils.linearContains(upperY, lowerY, ur.getY(), ll.getY())){
            Point relativePosition = position.sub(ll);
            applet.fill(rgb.getRed(),rgb.getGreen(), rgb.getBlue());
            if(radius < 0.1) radius = 0.1;
            applet.ellipse(
                    (float)(relativePosition.getX() * scale.getX() + bias.getX()),
                    (float)(relativePosition.getY() * scale.getY() + bias.getY()),
                    (float)(radius * 2 * scale.getX()),
                    (float)(radius * 2 * scale.getY()));
        }
    }

    private static void drawSegment(Segment segment, PApplet applet, Point ll, Point ur, Point scale, Point bias, Color rgb){
        Point position = segment.getPosition();
        double radius = segment.getRadius();
        double upperX =  position.getX() + radius;
        double lowerX = position.getX() - radius;
        double upperY = position.getY() + radius;
        double lowerY = position.getY() - radius;

        //applet.noStroke();

        //if we are visible on the screen
        if(Utils.linearContains(upperX, lowerX, ur.getX(), ll.getX()) &&
                Utils.linearContains(upperY, lowerY, ur.getY(), ll.getY())){
            Point relativePosition = position.sub(ll);
            applet.fill(rgb.getRed(),rgb.getGreen(), rgb.getBlue());
            applet.ellipse(
                    (float)(relativePosition.getX() * scale.getX() + bias.getX()),
                    (float)(relativePosition.getY() * scale.getY() + bias.getY()),
                    (float)(radius * 2 * scale.getX()),
                    (float)(radius * 2 * scale.getY()));
        }
    }

    public static void drawSnake(SnakeBody snake, PApplet applet, Point ll, Point ur, Point scale, Point bias) {
        List<Segment> bodySegments = snake.getSegments();
        if(snake.isBoosting())
            applet.stroke(Color.YELLOW.getRed(),Color.YELLOW.getGreen(),Color.YELLOW.getBlue());
        else
            applet.stroke(0);
        for(int i = snake.getSegments().size()-1; i >= 0; i--)
            drawSegment(bodySegments.get(i), applet, ll, ur, scale, bias, snake.getColor());
    }

    public static void drawFPS(PApplet applet, PFont font){
        applet.textFont(font,22);
        applet.fill(255);
        applet.text("Fps:" + String.valueOf(applet.frameRate).trim(),10,20);
    }

    public static void drawGeneration(PApplet applet, PFont font, int generation){
        applet.textFont(font,22);
        applet.fill(255);
        applet.text("Gen:" + generation,10,40);
    }

    public static void drawTickNum(PApplet applet, PFont font, int tickNum){
        applet.textFont(font,22);
        applet.fill(255);
        applet.text("Tick:" + tickNum,10,60);
    }

    public static void drawGrid(PApplet applet, Point ll, Point ur, Point scale, Point bias){
        applet.stroke(50);

        for(int x = 0; x < ur.getX() - ll.getX(); x+= 4){
            double offset = ll.getX()%4;
            Point relativeP1 = new Point(x - offset,0);
            Point relativeP2 = new Point(x - offset,ur.getY()- ll.getY());

            applet.line(
                    (float)(relativeP1.getX() * scale.getX() + bias.getX()),
                    (float)(relativeP1.getY() * scale.getY() + bias.getY()),
                    (float)(relativeP2.getX() * scale.getX() + bias.getX()),
                    (float)(relativeP2.getY() * scale.getY() + bias.getY())
            );
        }

        for(int y = 0; y < ur.getY() - ll.getY(); y+= 4){
            double offset = ll.getY()%4;
            Point relativeP1 = new Point(0,y - offset);
            Point relativeP2 = new Point(ur.getX()- ll.getX(), y - offset);

            applet.line(
                    (float)(relativeP1.getX() * scale.getX() + bias.getX()),
                    (float)(relativeP1.getY() * scale.getY() + bias.getY()),
                    (float)(relativeP2.getX() * scale.getX() + bias.getX()),
                    (float)(relativeP2.getY() * scale.getY() + bias.getY())
            );
        }
    }
}
