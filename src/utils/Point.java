package utils;

public class Point {
    private double x;
    private double y;

    public Point(){
        this(0,0);
    }

    public Point(Point p){
        this.x = p.x;
        this.y = p.y;
    }

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

    public void setY(double y) {
        this.y = y;
    }

    public void set(Point p){
        this.x = p.x;
        this.y = p.y;
    }

    public Point mult(double d){
        return new Point(x*d,y*d);
    }

    public Point div(Point p){
        return new Point(x/p.x, y/p.y);
    }

    public Point add(Point p){
        return new Point(x+p.x, y+p.y);
    }

    public Point sub(Point p){
        return add(p.mult(-1));
    }

    public Point go(double direction, double distance){
        return new Point(
                x + Math.cos(direction) * distance,
                y + Math.sin(direction) * distance);
    }

    public double angleTo(Point destination){
        return Math.atan2(destination.y-y, destination.x - x);
    }

    public double distanceTo(Point destination){
        return Math.sqrt(Math.pow(destination.y - y,2) + Math.pow(destination.x - x, 2));
    }
}
