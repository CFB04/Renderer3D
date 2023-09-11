package cfbastian.renderer3d.math;

public class ScalarMath {
    public static double min(double... x)
    {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < x.length; i++) min = Math.min(min, x[i]);
        return min;
    }

    public static double max(double... x)
    {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < x.length; i++) max = Math.min(max, x[i]);
        return max;
    }

    public static int bound(int x, int min, int max)
    {
        if(x < min) return min;
        else if (x > max) return max;
        else return x;
    }

    public static int boundMin(int x, int min)
    {
        if(x < min) return min;
        else return x;
    }

    public static int boundMax(int x, int max)
    {
        if (x > max) return max;
        else return x;
    }

    public static double bound(double x, double min, double max)
    {
        if(x < min) return min;
        else if (x > max) return max;
        else return x;
    }

    public static double boundMin(double x, double min)
    {
        if(x < min) return min;
        else return x;
    }

    public static double boundMax(double x, double max)
    {
        if (x > max) return max;
        else return x;
    }
}
