package cfbastian.renderer3d.entities;

import cfbastian.renderer3d.math.Vector3;

public class HorizontalPlane extends Entity {
    public HorizontalPlane(Vector3 pos, String key) {
        super(pos, key);
    }

    @Override
    public double getDistance(Vector3 cameraPos) {
        return cameraPos.y - pos.y;
    }

    @Override
    public Vector3 getNormal(Vector3 pos) {
        return new Vector3(0D, 1D, 0D);
    }
}
