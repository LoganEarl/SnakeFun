package utils;

public class MutableDouble {
    private double value;

    public MutableDouble(double value){
        this.value = value;
    }

    public double get() {
        return value;
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
