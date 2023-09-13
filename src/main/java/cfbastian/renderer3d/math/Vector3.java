package cfbastian.renderer3d.math;

public class Vector3 {
    public float x, y, z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float[] toArray()
    {
        return new float[]{x, y, z};
    }
}
