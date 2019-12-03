package utils;

public class Utils {
    public static boolean linearContains(double aUpper, double aLower, double bUpper, double bLower) {
        if(aUpper > bUpper && aLower <= bUpper) return true;
        if(aLower < bLower && aUpper >= bLower) return true;
        return aUpper <= bUpper && aLower >= bLower;
    }
}
