package cfbastian.renderer3d;

import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;
import com.aparapi.Kernel;
import com.aparapi.Range;

public class Camera {
    private Vector3 pos, dir;
    private float focalLength, fov, viewportHeight, viewportWidth;
    private float uSpacing, vSpacing;
    private Vector3 deltaU, deltaV;
    private Vector3 viewportCenter, viewportUpperLeft, upperLeftPixel; // Center is not actually rendered when either width or height is odd
    private float theta, phi; // Theta is angle off the positive x-axis, phi is angle off the (vertical) positive z-axis
    private float[] rays;
    private int[] kIdxs;
    private float[] shearFactors;

    private int numRays;

    Range range;
    RayGenerationKernel rayGenerationKernel;

    public Camera(Vector3 pos, float theta, float phi, float fov) {
        this.pos = pos;
        this.theta = theta;
        this.phi = phi;
        this.fov = fov;

        this.viewportWidth = 2.0f;
        this.viewportHeight = viewportWidth * (float) Application.height / (float) Application.width;

        this.dir = new Vector3((float) (Math.sin(phi)*Math.cos(theta)), (float) (Math.sin(phi)*Math.sin(theta)), (float) Math.cos(phi));
        this.focalLength = 0.5f * viewportWidth / ((float) Math.tan(Math.PI * this.fov / 360f));
        this.viewportCenter = VectorMath.scale(dir, focalLength);

        this.uSpacing = viewportWidth / (float) (Application.width);
        this.vSpacing = viewportHeight / (float) (Application.height);
        this.deltaU = new Vector3((float) (uSpacing*Math.sin(theta)), (float) (-uSpacing*Math.cos(theta)), 0f);
        this.deltaV = new Vector3((float) (vSpacing*Math.cos(phi)*Math.cos(theta)), (float) (vSpacing*Math.cos(phi)*Math.sin(theta)), (float) (-vSpacing*Math.sin(phi)));
        this.viewportUpperLeft = VectorMath.add(viewportCenter, VectorMath.add(VectorMath.scale(deltaU, -Application.width / 2f), VectorMath.scale(deltaV, -Application.height / 2f)));
        this.upperLeftPixel = VectorMath.add(viewportUpperLeft, VectorMath.scale(deltaU, 0.5f), VectorMath.scale(deltaV, 0.5f));

        this.numRays = MainController.getPixelsLength();
        this.rays = new float[numRays * 3];

        range = Range.create(numRays, 32);
        rayGenerationKernel = new RayGenerationKernel();

        calculateRays();
    }

    public Vector3 getPos() {
        return pos;
    }

    public void setPos(Vector3 pos) {
        this.pos = pos;
    }

    public Vector3 getDir() {
        return dir;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
        this.focalLength = 0.5f * viewportWidth / ((float) Math.tan(Math.PI * this.fov / 360f));
    }

    public float getUSpacing() {
        return uSpacing;
    }

    public float getVSpacing() {
        return vSpacing;
    }

    public float getTheta() {
        return theta;
    }

    public float getPhi() {
        return phi;
    }

    public void setAngle(float theta, float phi)
    {
        this.theta = theta;// % Math.PI*2;
        this.phi = ScalarMath.bound(phi, 0f, (float) Math.PI);
        this.dir = new Vector3((float) (Math.sin(phi)*Math.cos(theta)), (float) (Math.sin(phi)*Math.sin(theta)), (float) Math.cos(phi));
        this.viewportCenter = VectorMath.scale(dir, focalLength);
        this.deltaU = new Vector3((float) (uSpacing*Math.sin(theta)), (float) (-uSpacing*Math.cos(theta)), 0f);
        this.deltaV = new Vector3((float) (vSpacing*Math.cos(phi)*Math.cos(theta)), (float) (vSpacing*Math.cos(phi)*Math.sin(theta)), (float) (-vSpacing*Math.sin(phi)));
        this.viewportUpperLeft = VectorMath.add(viewportCenter, VectorMath.add(VectorMath.scale(deltaU, -Application.width / 2f), VectorMath.scale(deltaV, -Application.height / 2f)));
        this.upperLeftPixel = VectorMath.add(viewportUpperLeft, VectorMath.scale(deltaU, 0.5f), VectorMath.scale(deltaV, 0.5f));
        calculateRays();
    }

    public void calculateRays()
    {
//        rayGenerationKernel.update(deltaU, deltaV, upperLeftPixel, numRays);
//        rayGenerationKernel.execute(range);
//        rays = rayGenerationKernel.getRays();
//        kIdxs = rayGenerationKernel.getkIdxs();
//        shearFactors = rayGenerationKernel.getShearFactors();

        float[] us = new float[Application.width * 3];
        float[] vs = new float[Application.height * 3];

        for (int i = 0; i < Application.width; i++)
        {
            float[] u = VectorMath.scale(deltaU, i).toArray();
            us[i*3] = u[0];
            us[i*3+1] = u[1];
            us[i*3+2] = u[2];
        }

        for (int i = 0; i < Application.height; i++)
        {
            float[] v = VectorMath.scale(deltaV, i).toArray();
            vs[i*3] = v[0];
            vs[i*3+1] = v[1];
            vs[i*3+2] = v[2];
        }

        float[] upperLeftPixel = this.upperLeftPixel.toArray();

        for (int i = 0; i < numRays; i++) {
            int x = i%Application.width, y = i/Application.width;
            rays[3*i] = upperLeftPixel[0] + us[x*3] + vs[y*3];
            rays[3*i+1] = upperLeftPixel[1] + us[x*3+1] + vs[y*3+1];
            rays[3*i+2] = upperLeftPixel[2] + us[x*3+2] + vs[y*3+2];
        }
        precalculation();
    }

    public void precalculation()
    {
        kIdxs = new int[rays.length];
        shearFactors = new float[rays.length];

        for (int i = 0; i < rays.length/3; i++) {
            float[] absray = new float[]{Math.abs(rays[i*3]), Math.abs(rays[i*3+1]), Math.abs(rays[i*3+2])};
            int kz = absray[0]>absray[1]? 0 : (absray[1]>absray[2]? 1 : 2);
            int kx = kz + 1 == 3? 0: kz + 1;
            int ky = kx + 1 == 3? 0: kx + 1;

            if(rays[i*3 + kz] < 0D)
            {
                int a = kx;
                kx = ky;
                ky = a;
            }

            kIdxs[i*3] = kx;
            kIdxs[i*3+1] = ky;
            kIdxs[i*3+2] = kz;
            shearFactors[i*3] = rays[i*3 + kx]/rays[i*3 + kz];   //Sx
            shearFactors[i*3+1] = rays[i*3 + ky]/rays[i*3 + kz]; //Sy
            shearFactors[i*3+2] = 1f/rays[i*3 + kz];             //Sz
        }
    }

    public float[] getRays()
    {
        return rays;
    }

    public int[] getKIdxs()
    {
        return kIdxs;
    }

    public float[] getShearFactors()
    {
        return shearFactors;
    }

    private class RayGenerationKernel extends Kernel
    {
        //Ray generation fields
        @Local private int width;
        @Local private float[] us, vs;
        @Local private float[] upperLeftPixel;
        @Local private float[] tempRay;
        @Local private float[] rays;

        // Precalculation fields
        @Local private float[] absray = new float[3];
        @Local private int[] kIdxs;
        @Local private float[] shearFactors;

        public synchronized void update(Vector3 deltaU, Vector3 deltaV, Vector3 upperLeftPixel, int numRays)
        {
            this.width = Application.width;
            this.us = new float[Application.width * 3];
            this.vs = new float[Application.height * 3];
            this.upperLeftPixel = upperLeftPixel.toArray();
            this.tempRay = new float[numRays];
            this.rays = new float[numRays * 3];

            this.kIdxs = new int[numRays*3];
            this.shearFactors = new float[numRays*3];

            for (int i = 0; i < Application.width; i++)
            {
                float[] u = VectorMath.scale(deltaU, i).toArray();
                us[i*3] = u[0];
                us[i*3+1] = u[1];
                us[i*3+2] = u[2];
            }

            for (int i = 0; i < Application.height; i++)
            {
                float[] v = VectorMath.scale(deltaV, i).toArray();
                vs[i*3] = v[0];
                vs[i*3+1] = v[1];
                vs[i*3+2] = v[2];
            }
        }

        public float[] getRays() {
            return rays;
        }

        public int[] getkIdxs() {
            return kIdxs;
        }

        public float[] getShearFactors() {
            return shearFactors;
        }

        @Override
        public void run()
        {
            int i = getGlobalId();
            int x = i%width, y = i/width;

            // Ray generation
            tempRay[0] = upperLeftPixel[0] + us[x*3] + vs[y*3];
            tempRay[1] = upperLeftPixel[1] + us[x*3+1] + vs[y*3+1];
            tempRay[2] = upperLeftPixel[2] + us[x*3+2] + vs[y*3+2];

            // Precalculation
            absray[0] = Math.abs(tempRay[0]);
            absray[1] = Math.abs(tempRay[1]);
            absray[2] = Math.abs(tempRay[2]);

            int kz = absray[0]>absray[1]? 0 : (absray[1]>absray[2]? 1 : 2);
            int kx = kz + 1 == 3? 0: kz + 1;
            int ky = kx + 1 == 3? 0: kx + 1;

            if(tempRay[kz] < 0D)
            {
                int a = kx;
                kx = ky;
                ky = a;
            }

            rays[i*3] = tempRay[0];
            rays[i*3+1] = tempRay[1];
            rays[i*3+2] = tempRay[2];
            kIdxs[i*3] = kx;
            kIdxs[i*3+1] = ky;
            kIdxs[i*3+2] = kz;
            shearFactors[i*3] = rays[i*3 + kx]/rays[i*3 + kz];   //Sx
            shearFactors[i*3+1] = rays[i*3 + ky]/rays[i*3 + kz]; //Sy
            shearFactors[i*3+2] = 1f/rays[i*3 + kz];             //Sz
        }
    }
}
