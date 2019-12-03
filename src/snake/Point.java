package snake;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public Point mult(double d){
        return new Point(x*d,y*d);
    }

    public Point add(Point p){
        return new Point(x+p.x, y+p.y);
    }

    public Point sub(Point p){
        return add(p.mult(-1));
    }

    public void setY(double y) {
        this.y = y;
    }
}
