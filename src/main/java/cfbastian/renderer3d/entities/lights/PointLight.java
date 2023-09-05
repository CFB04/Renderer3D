package cfbastian.renderer3d.entities.lights;

import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;

public class PointLight extends Light{
    public PointLight(Vector3 pos, double brightness, String key) {
        super(pos, brightness, key);
    }

    @Override
    public Vector3 getNormal(Vector3 pos) {
        return new Vector3(0D, 0D, 0D);
    }

    @Override
    public double getLight(Vector3 pos, Vector3 normal) {
        Vector3 difference = VectorMath.subtract(this.pos, pos);
        return brightness * VectorMath.dot(VectorMath.normalize(difference), normal) * Math.pow(Math.E, -Math.abs(VectorMath.length(difference)));
    }
}
