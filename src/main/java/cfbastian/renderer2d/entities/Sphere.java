package cfbastian.renderer2d.entities;

import cfbastian.renderer2d.math.Vector3;
import cfbastian.renderer2d.math.VectorMath;

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
    public Vector3 getNormal(Vector3 pos) {
        return VectorMath.normalize(VectorMath.subtract(pos, this.pos));
    }
}
