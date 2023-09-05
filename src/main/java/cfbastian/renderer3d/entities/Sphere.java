package cfbastian.renderer3d.entities;

import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;

public class Sphere extends Entity {
    double r;

    public Sphere(Vector3 pos, double r, String key) {
        super(pos, key);
        this.r = r;
    }

    @Override
    public double getDistance(Vector3 cameraPos) {
        return VectorMath.distance(cameraPos, pos) - r;
    }

    @Override
    public double getDistance(float[] cameraPos) {
        float x = (float) (cameraPos[0] - pos.x), y = (float) (cameraPos[1] - pos.y), z = (float) (cameraPos[2] - pos.z);
        return Math.sqrt(x*x + y*y + z*z) - r;
    }

    @Override
    public Vector3 getNormal(Vector3 pos) {
        return VectorMath.normalize(VectorMath.subtract(pos, this.pos));
    }
}
