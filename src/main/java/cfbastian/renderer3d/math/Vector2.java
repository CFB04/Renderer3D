package cfbastian.renderer3d.math;

public class Vector2 {
    public double x, y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double[] toArray()
    {
        return new double[]{x, y};
    }
}
