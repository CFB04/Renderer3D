package cfbastian.renderer3d.entities.lights;

import cfbastian.renderer3d.entities.Entity;
import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;

public abstract class Light extends Entity {
    double brightness;

    public Light(Vector3 pos, double brightness, String key) {
        super(pos, key);
        this.brightness = brightness;
    }

    @Override
    public double getDistance(Vector3 cameraPos) {
        return VectorMath.distance(cameraPos, pos);
    }

    @Override
    public abstract Vector3 getNormal(Vector3 pos);

    public abstract double getLight(Vector3 pos, Vector3 normal);
}
