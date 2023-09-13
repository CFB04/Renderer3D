package cfbastian.renderer3d.math;

public class ScalarMath {
    public static float min(float... x)
    {
        float min = Float.MAX_VALUE;
        for (float v : x) min = Math.min(min, v);
        return min;
    }

    public static float max(float... x)
    {
        float max = Float.MIN_VALUE;
        for (float v : x) max = Math.min(max, v);
        return max;
    }

    public static int bound(int x, int min, int max)
    {
        if(x < min) return min;
        else return Math.min(x, max);
    }

    public static int boundMin(int x, int min)
    {
        return Math.max(x, min);
    }

    public static int boundMax(int x, int max)
    {
        return Math.min(x, max);
    }

    public static float bound(float x, float min, float max)
    {
        if(x < min) return min;
        else return Math.min(x, max);
    }

    public static float boundMin(float x, float min)
    {
        return Math.max(x, min);
    }

    public static float boundMax(float x, float max)
    {
        return Math.min(x, max);
    }

    public static float weightedAvg(float a, float b, float w)
    {
        return a * (1f - w) + b * w;
    }

    public static float sin(float x)
    {
        return (float) Math.sin(x);
    }
}
