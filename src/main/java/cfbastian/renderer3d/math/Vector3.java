package cfbastian.renderer3d.math;

public class Vector3 {
    public double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double[] toArray()
    {
        return new double[]{x, y, z};
    }
}
