package cfbastian.renderer3d.math;

public class ScalarMath {
    public static float min(float... x)
    {
        float min = Float.MAX_VALUE;
        for (int i = 0; i < x.length; i++) min = Math.min(min, x[i]);
        return min;
    }

    public static float max(float... x)
    {
        float max = Float.MIN_VALUE;
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

    public static float bound(float x, float min, float max)
    {
        if(x < min) return min;
        else if (x > max) return max;
        else return x;
    }

    public static float boundMin(float x, float min)
    {
        if(x < min) return min;
        else return x;
    }

    public static float boundMax(float x, float max)
    {
        if (x > max) return max;
        else return x;
    }

    public static float weightedAvg(float a, float b, float w)
    {
        return a * (1f - w) + b * w;
    }
}
