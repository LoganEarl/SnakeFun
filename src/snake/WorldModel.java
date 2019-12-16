package snake;

import utils.Point;

import java.awt.*;
import java.util.*;
import java.util.List;

public class WorldModel {
    private Set<SnakeBody> livingSnakes = new HashSet<>();
    private Set<SnakeBody> deadSnakes = new HashSet<>();
    private Set<SnakeBody> playerSnakes = new HashSet<>();
    private Point size;
    private Random random = new Random(System.currentTimeMillis());
    private static final int FOOD_MAP_RESOLUTION = 16;
    private Map<Point, List<Food>> foodPlacing = new HashMap<>();

    private double minFood, maxFood;

    //foodDensity represents how many food parts should exist on average per 10x10 square
    public WorldModel(int numSnakes, Point size, boolean hasPlayer, double foodDensity, double minFood, double maxFood) {
        this.minFood = minFood;
        this.maxFood = maxFood;
        this.size = size;

        if (hasPlayer) {
            playerSnakes.add(new SimpleSnakeBody(10, randomPosition(random, size), Color.GREEN));
        }
        for (int i = 0; i < numSnakes; i++) {
            livingSnakes.add(new SimpleSnakeBody(10, randomPosition(random, size),
                    Color.getHSBColor(random.nextFloat(), random.nextFloat(), random.nextFloat())));
        }
        livingSnakes.addAll(playerSnakes);
        placeInitialFood(foodDensity);
    }

    private static Point randomPosition(Random rnd, Point size) {
        return new Point((rnd.nextDouble() * size.getX() - size.getX() / 2),
                (rnd.nextDouble() * size.getY() - size.getY() / 2));
    }

    public void tick() {
        for (SnakeBody s : livingSnakes) {
            s.simulationTick();
        }
    }

    private void placeInitialFood(double density) {
        int numFood = (int) (density / 100 * size.getX() * size.getY());
        while (numFood > 0) {
            numFood--;
            addFood();
        }
    }

    public void doSnakeCollisions() {
        //TODO collide players with borders

        //collide players with each other
        List<SnakeBody> allSnakes = new ArrayList<>(this.livingSnakes);
        for (int i = 0; i < allSnakes.size() - 1; i++) {
            for (int j = i+1; j < allSnakes.size(); j++) {
                SnakeBody s1 = allSnakes.get(i);
                SnakeBody s2 = allSnakes.get(j);

                if (s1.isDead() || s2.isDead()) continue;

                if (s1.headCollidingWith(s2.getHead().getPosition(), s2.getHead().getRadius())) {
                    if (s1.getSegments().size() < s2.getSegments().size())
                        s1.kill();
                    else if (s1.getSegments().size() > s2.getSegments().size())
                        s2.kill();
                    else {
                        s1.kill();
                        s2.kill();
                    }
                }else if (s2.bodyCollidingWith(s1.getHead().getPosition(), s1.getHead().getRadius()))
                    s1.kill();
                else if (s1.bodyCollidingWith(s2.getHead().getPosition(), s2.getHead().getRadius()))
                    s2.kill();
            }
        }

        //substitute dead players with food
        Iterator<SnakeBody> snakeBodyIterator = livingSnakes.iterator();
        while(snakeBodyIterator.hasNext()){
            SnakeBody snake = snakeBodyIterator.next();
            if(snake.isDead()){
                decompose(snake);
                deadSnakes.add(snake);
                snakeBodyIterator.remove();
            }
        }
    }

    public void doFoodCollisions(){
        //collide players with food source
        //TODO see if optimization is possible here
        for(SnakeBody snake: livingSnakes){
            List<Food> eatenFood = getFoodNear(snake.getHead().getPosition(), snake.getHead().getRadius());
            for(Food f: eatenFood){
                snake.addFood(f.getAmount());
                removeFood(f);
            }
        }
    }

    private void decompose(SnakeBody deadSnake){

    }

    public void addFood() {
        double foodAmt = random.nextDouble() * (maxFood - minFood) + minFood;
        addFood(randomPosition(random, size), foodAmt);
    }

    public void addFood(Point position, double amount) {
        Food f = new Food(amount, position);
        Point foodCords = toFoodMapCords(position);
        if (!foodPlacing.containsKey(foodCords))
            foodPlacing.put(foodCords, new ArrayList<>(2));
        foodPlacing.get(foodCords).add(f);
    }

    public void removeFood(Food f) {
        Point foodCords = toFoodMapCords(f.getPosition());
        if (foodPlacing.containsKey(foodCords))
            foodPlacing.get(foodCords).remove(f);
    }

    public List<Food> getFoodNear(Point p, double radius) {
        List<Point> nearby = new LinkedList<>();

        Point fmp = toFoodMapCords(p);
        double fmr = radius / FOOD_MAP_RESOLUTION;

        List<Food> food = new LinkedList<>();

        for (List<Food> l : foodPlacing.values())
            for (Food f : l)
                if (f.getPosition().distanceTo(p) < radius)
                    food.add(f);
        return food;
    }

    public List<Food> getFoodWithin(Point ll, Point ur) {
        Point from = toFoodMapCords(ll);
        Point to = toFoodMapCords(ur);

        List<Food> food = new LinkedList<>();
        Point walker = new Point();
        for (int y = (int) from.getY(); y <= to.getY(); y++) {
            for (int x = (int) from.getX(); x <= to.getX(); x++) {
                walker.setX(x);
                walker.setY(y);
                if (foodPlacing.containsKey(walker))
                    food.addAll(foodPlacing.get(walker));
            }
        }

        Iterator<Food> foodIterator = food.iterator();
        while (foodIterator.hasNext()) {
            Food f = foodIterator.next();
            Point pos = f.getPosition();
            if (!pos.containedBy(ur, ll))
                foodIterator.remove();
        }
        return food;
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    private static Point toFoodMapCords(Point in) {
        return new Point(
                ((int) in.getX()) / FOOD_MAP_RESOLUTION,
                ((int) in.getY()) / FOOD_MAP_RESOLUTION
        );
    }

    public SnakeBody getPlayer() {
        return playerSnakes.iterator().next();
    }

    public Set<SnakeBody> getLivingSnakes() {
        return livingSnakes;
    }
}
