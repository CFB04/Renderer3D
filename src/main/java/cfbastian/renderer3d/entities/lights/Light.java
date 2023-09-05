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
    public double getDistance(float[] cameraPos) {
        float x = (float) (cameraPos[0] - pos.x), y = (float) (cameraPos[1] - pos.y), z = (float) (cameraPos[2] - pos.z);
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public abstract Vector3 getNormal(Vector3 pos);

    public abstract double getLight(Vector3 pos, Vector3 normal);
}
