package snake;

import utils.Point;

import java.util.Objects;

public class Food {
    private double amount;
    private Point position;

    public Food(double amount, Point position) {
        this.amount = amount;
        this.position = position;
    }

    public double getAmount() {
        return amount;
    }

    public Point getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return Double.compare(food.amount, amount) == 0 &&
                position.equals(food.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, position);
    }
}
