package cfbastian.renderer3d.bodies;

public class AxisAlignedBoundingBox {
    private float[] a, b;
    private int[] faces;

    public AxisAlignedBoundingBox(float[] a, float[] b) {
        this.a = a;
        this.b = b;
    }

    public AxisAlignedBoundingBox(int[] faces, float[] vertices) {
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
    }

    public boolean hitAABB(float[] cameraPos, float[] ray, float padding)
    {
        float[] a1 = new float[3];
        float[] b1 = new float[3];
        a1[0] = Math.min(a[0], b[0]) - padding;
        a1[1] = Math.min(a[1], b[1]) - padding;
        a1[2] = Math.min(a[2], b[2]) - padding;
        b1[0] = Math.max(a[0], b[0]) + padding;
        b1[1] = Math.max(a[1], b[1]) + padding;
        b1[2] = Math.max(a[2], b[2]) + padding;

        float tMin = Float.MIN_VALUE, tMax = Float.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            float invD = 1f/ray[i];
            float t0 = (a1[i] - cameraPos[i]) * invD;
            float t1 = (b1[i] - cameraPos[i]) * invD;

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
}
