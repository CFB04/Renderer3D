package cfbastian.renderer3d.entities;

import cfbastian.renderer3d.math.Vector3;

public abstract class Entity {
    protected Vector3 pos;

    private String key;

    public Entity(Vector3 pos, String key) {
        this.pos = pos;
        this.key = key;
    }

    public abstract double getDistance(Vector3 cameraPos);

    public abstract double getDistance(float[] cameraPos);

    public abstract Vector3 getNormal(Vector3 pos);

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public String getKey() {
        return key;
    }
}
