package cfbastian.renderer3d.bodies;

public class AxisAlignedBoundingBox {
    private float[] a, b;
    private int[] faces;
    private float padding;

    public AxisAlignedBoundingBox(float[] a, float[] b, float padding) {
        this.a = new float[3];
        this.b = new float[3];
        this.a[0] = Math.min(a[0], b[0]) - padding;
        this.a[1] = Math.min(a[1], b[1]) - padding;
        this.a[2] = Math.min(a[2], b[2]) - padding;
        this.b[0] = Math.max(a[0], b[0]) + padding;
        this.b[1] = Math.max(a[1], b[1]) + padding;
        this.b[2] = Math.max(a[2], b[2]) + padding;
    }

    public AxisAlignedBoundingBox(int[] faces, float[] vertices, float padding) {
        this.faces = faces;

        a = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
        b = new float[]{Float.MIN_NORMAL, Float.MIN_NORMAL, Float.MIN_NORMAL};

        for (int face : faces) {
            a[0] = Math.min(a[0], vertices[face * 3]);
            a[1] = Math.min(a[1], vertices[face * 3 + 1]);
            a[2] = Math.min(a[2], vertices[face * 3 + 2]);
            b[0] = Math.max(b[0], vertices[face * 3]);
            b[1] = Math.max(b[1], vertices[face * 3 + 1]);
            b[2] = Math.max(b[2], vertices[face * 3 + 2]);
        }
        a[0] -= padding;
        a[1] -= padding;
        a[2] -= padding;
        b[0] += padding;
        b[1] += padding;
        b[2] += padding;
    }

    public boolean hitAABB(float[] cameraPos, float[] ray)
    {
        float tMin = Float.MIN_VALUE, tMax = Float.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            float invD = 1f/ray[i];
            float t0 = (a[i] - cameraPos[i]) * invD;
            float t1 = (b[i] - cameraPos[i]) * invD;

            if(invD < 0f)
            {
                float v = t0;
                t0 = t1;
                t1 = v;
            }

            if(t0 > tMin) tMin = t0;
            if(t1 < tMax) tMax = t1;

            if(tMax < tMin) return false;
        }

        return true;
    }

    public float[] getA() {
        return a;
    }

    public float[] getB() {
        return b;
    }

    public float[] getAB()
    {
        float[] ret = new float[6];
        ret[0] = a[0];
        ret[1] = a[1];
        ret[2] = a[2];
        ret[3] = b[0];
        ret[4] = b[1];
        ret[5] = b[2];
        return ret;
    }

    public float getHalfSurfaceArea()
    {
        float w = b[1] - a[1], h = b[2] - a[2], d = b[0] - a[0];
        return w * h + w * d + d * h;
    }

    public int[] getFaces() {
        return faces;
    }
}
