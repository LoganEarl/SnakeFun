package snake;

import utils.Point;

import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldModel {
    private Set<SnakeBody> npcSnakes = new HashSet<>();
    private SnakeBody player;
    private Point size;
    private Random random = new Random(System.currentTimeMillis());

    public WorldModel(int numSnakes, Point size, boolean hasPlayer){
        if(hasPlayer){
            player = new SimpleSnakeBody(10, randomPosition(random, size), Color.GREEN);
        }
        for(int i = 0; i < numSnakes; i++){
            npcSnakes.add(new SimpleSnakeBody(10, randomPosition(random, size),
                    Color.getHSBColor(random.nextFloat(),random.nextFloat(),random.nextFloat())));
        }
    }

    private static Point randomPosition(Random rnd, Point size){
        return new Point((rnd.nextDouble() * size.getX() - size.getX()/2),
                (rnd.nextDouble() * size.getY() - size.getY()/2));
    }

    public SnakeBody getPlayer(){
        return player;
    }

    public Set<SnakeBody> getNPCSnakes(){
        return npcSnakes;
    }
}
