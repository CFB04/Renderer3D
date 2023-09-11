package cfbastian.renderer3d.math;

public final class VectorMath {
    public static Vector2 add(Vector2 v1, Vector2 v2)
    {
        return new Vector2(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vector2 add(Vector2... vs)
    {
        Vector2 vRet = new Vector2(0D,0D);
        for (Vector2 v : vs) {
            vRet.x += v.x;
            vRet.y += v.y;
        }
        return vRet;
    }

    public static IntVector2 add(IntVector2 v1, IntVector2 v2)
    {
        return new IntVector2(v1.x + v2.x, v1.y + v2.y);
    }

    public static IntVector2 add(IntVector2... vs)
    {
        IntVector2 vRet = new IntVector2(0,0);
        for (IntVector2 v : vs) {
            vRet.x += v.x;
            vRet.y += v.y;
        }
        return vRet;
    }

    public static Vector3 add(Vector3 v1, Vector3 v2)
    {
        return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    public static Vector3 add(Vector3... vs)
    {
        Vector3 vRet = new Vector3(0D,0D, 0D);
        for (Vector3 v : vs) {
            vRet.x += v.x;
            vRet.y += v.y;
            vRet.z += v.z;
        }
        return vRet;
    }

    public static IntVector3 add(IntVector3 v1, IntVector3 v2)
    {
        return new IntVector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    public static IntVector3 add(IntVector3... vs)
    {
        IntVector3 vRet = new IntVector3(0,0, 0);
        for (IntVector3 v : vs) {
            vRet.x += v.x;
            vRet.y += v.y;
            vRet.z += v.z;
        }
        return vRet;
    }

    public static Vector2 subtract(Vector2 v1, Vector2 v2)
    {
        return new Vector2(v1.x - v2.x, v1.y - v2.y);
    }

    public static IntVector2 subtract(IntVector2 v1, IntVector2 v2)
    {
        return new IntVector2(v1.x - v2.x, v1.y - v2.y);
    }

    public static Vector3 subtract(Vector3 v1, Vector3 v2)
    {
        return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public static IntVector3 subtract(IntVector3 v1, IntVector3 v2)
    {
        return new IntVector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public static Vector2 multiply(Vector2 v1, Vector2 v2)
    {
        return new Vector2(v1.x * v2.x, v1.y * v2.y);
    }

    public static Vector2 multiply(Vector2... vs)
    {
        Vector2 vRet = new Vector2(1D,1D);
        for (Vector2 v : vs) {
            vRet.x *= v.x;
            vRet.y *= v.y;
        }
        return vRet;
    }

    public static IntVector2 multiply(IntVector2 v1, IntVector2 v2)
    {
        return new IntVector2(v1.x * v2.x, v1.y * v2.y);
    }

    public static IntVector2 multiply(IntVector2... vs)
    {
        IntVector2 vRet = new IntVector2(1,1);
        for (IntVector2 v : vs) {
            vRet.x *= v.x;
            vRet.y *= v.y;
        }
        return vRet;
    }

    public static Vector3 multiply(Vector3 v1, Vector3 v2)
    {
        return new Vector3(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
    }

    public static Vector3 multiply(Vector3... vs)
    {
        Vector3 vRet = new Vector3(1D,1D, 1D);
        for (Vector3 v : vs) {
            vRet.x *= v.x;
            vRet.y *= v.y;
            vRet.z *= v.z;
        }
        return vRet;
    }

    public static IntVector3 multiply(IntVector3 v1, IntVector3 v2)
    {
        return new IntVector3(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
    }

    public static IntVector3 multiply(IntVector3... vs)
    {
        IntVector3 vRet = new IntVector3(1,1, 1);
        for (IntVector3 v : vs) {
            vRet.x *= v.x;
            vRet.y *= v.y;
            vRet.z *= v.z;
        }
        return vRet;
    }

    public static Vector2 scale(Vector2 v1, double s)
    {
        return new Vector2(v1.x * s, v1.y * s);
    }

    public static IntVector2 scale(IntVector2 v1, int s)
    {
        return new IntVector2(v1.x * s, v1.y * s);
    }

    public static Vector3 scale(Vector3 v1, double s)
    {
        return new Vector3(v1.x * s, v1.y * s, v1.z * s);
    }

    public static IntVector3 scale(IntVector3 v1, int s)
    {
        return new IntVector3(v1.x * s, v1.y * s, v1.z * s);
    }

    public static Vector2 divide(Vector2 v1, Vector2 v2)
    {
        return new Vector2(v1.x / v2.x, v1.y / v2.y);
    }

    public static IntVector2 divide(IntVector2 v1, IntVector2 v2)
    {
        return new IntVector2(v1.x / v2.x, v1.y / v2.y);
    }

    public static Vector3 divide(Vector3 v1, Vector3 v2)
    {
        return new Vector3(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z);
    }

    public static IntVector3 divide(IntVector3 v1, IntVector3 v2)
    {
        return new IntVector3(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z);
    }

    public static double dot(Vector2 v1, Vector2 v2)
    {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public static double dot(Vector2... vs)
    {
        Vector2 v = multiply(vs);
        return v.x + v.y;
    }

    public static double dot(IntVector2 v1, IntVector2 v2)
    {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public static double dot(IntVector2... vs)
    {
        IntVector2 v = multiply(vs);
        return v.x + v.y;
    }

    public static double dot(Vector3 v1, Vector3 v2)
    {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static double dot(Vector3... vs)
    {
        Vector3 v = multiply(vs);
        return v.x + v.y + v.z;
    }

    public static double dot(IntVector3 v1, IntVector3 v2)
    {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static double dot(IntVector3... vs)
    {
        IntVector3 v = multiply(vs);
        return v.x + v.y + v.z;
    }

    public static Vector2 normalize(Vector2 v)
    {
        double length = VectorMath.length(v);
        return new Vector2(v.x/length, v.y/length);
    }
    
    public static Vector3 normalize(Vector3 v)
    {
        double length = VectorMath.length(v);
        return new Vector3(v.x/length, v.y/length, v.z/length);
    }

    public static double length(Vector2 v)
    {
        return Math.sqrt(v.x * v.x + v.y * v.y);
    }

    public static double length(Vector3 v)
    {
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }

    public static double distance(Vector2 v1, Vector2 v2)
    {
        return length(subtract(v1, v2));
    }

    public static double distance(Vector3 v1, Vector3 v2)
    {
        return length(subtract(v1, v2));
    }

    public static double angleBetween(Vector3 v1, Vector3 v2)
    {
        return Math.acos(dot(v1, v2) / (length(v1) * length(v2)));
    }

    public static double[] cross(double[] v1, double[] v2)
    {
        return new double[]{v1[1]*v2[2] - v2[1]*v1[2], v1[0]*v2[2] - v2[0]*v1[2], v1[0]*v2[1] - v2[0]*v1[1]};
    }
}
