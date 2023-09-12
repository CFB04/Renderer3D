package cfbastian.renderer3d;

import cfbastian.renderer3d.math.IntVector3;
import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;

import java.util.Arrays;

public class Camera {
    private Vector3 pos, dir;
    private double focalLength, fov, viewportHeight, viewportWidth, halfViewportDiagonal;
    private double uSpacing, vSpacing;
    private Vector3 deltaU, deltaV;
    private Vector3 viewportCenter, viewportUpperLeft, upperLeftPixel; // Center is not actually rendered when either width or height is odd
    private double theta, phi; // Theta is angle off the positive x-axis, phi is angle off the (vertical) positive z-axis
    private double[] rays;
    private int[] kIdxs;
    private double[] shearFactors;

    public Camera(Vector3 pos, double theta, double phi, double fov) {
        this.pos = pos;
        this.theta = theta;
        this.phi = phi;
        this.fov = fov;

        this.viewportWidth = 2.0;
        this.viewportHeight = viewportWidth * (double) Application.HEIGHT / (double) Application.WIDTH;
        this.halfViewportDiagonal = Math.sqrt(viewportWidth*viewportWidth + viewportHeight*viewportHeight)/2D;

        this.dir = new Vector3(Math.sin(phi)*Math.cos(theta), Math.sin(phi)*Math.sin(theta), Math.cos(phi));
        this.focalLength = 1D; //2D * viewportWidth / (Math.tan(Math.PI * this.fov / 90D)); TODO implement FOV
        this.viewportCenter = VectorMath.add(pos, VectorMath.scale(dir, focalLength));
        System.out.println("Focal length: " + focalLength);

        this.uSpacing = viewportWidth / (double) (Application.WIDTH);
        this.vSpacing = viewportHeight / (double) (Application.HEIGHT);
        this.deltaU = new Vector3(uSpacing*Math.sin(theta), -uSpacing*Math.cos(theta), 0D);
        this.deltaV = new Vector3(vSpacing*Math.cos(phi)*Math.cos(theta), vSpacing*Math.cos(phi)*Math.sin(theta), -vSpacing*Math.sin(phi));
        this.viewportUpperLeft = VectorMath.add(viewportCenter, VectorMath.add(VectorMath.scale(deltaU, -Application.WIDTH / 2D), VectorMath.scale(deltaV, -Application.HEIGHT / 2D)));
        this.upperLeftPixel = VectorMath.add(viewportUpperLeft, VectorMath.scale(deltaU, 0.5), VectorMath.scale(deltaV, 0.5));
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

    public double getFov() {
        return fov;
    }

    public void setFov(double fov) {
        this.fov = fov;
        this.focalLength = this.viewportWidth / (Math.atan(this.fov * 0.5));
    }

    public double getuSpacing() {
        return uSpacing;
    }

    public double getvSpacing() {
        return vSpacing;
    }

    public double getTheta() {
        return theta;
    }

    public double getPhi() {
        return phi;
    }

    public void setAngle(double theta, double phi)
    {
        this.theta = theta;// % Math.PI*2;
        this.phi = ScalarMath.bound(phi, 0, Math.PI);
        this.dir = new Vector3(Math.sin(phi)*Math.cos(theta), Math.sin(phi)*Math.sin(theta), Math.cos(phi));
        this.viewportCenter = VectorMath.add(pos, VectorMath.scale(dir, focalLength));
        this.deltaU = new Vector3(uSpacing*Math.sin(theta), -uSpacing*Math.cos(theta), 0D);
        this.deltaV = new Vector3(vSpacing*Math.cos(phi)*Math.cos(theta), vSpacing*Math.cos(phi)*Math.sin(theta), -vSpacing*Math.sin(phi));
        this.viewportUpperLeft = VectorMath.add(viewportCenter, VectorMath.add(VectorMath.scale(deltaU, -Application.WIDTH / 2D), VectorMath.scale(deltaV, -Application.HEIGHT / 2D)));
        this.upperLeftPixel = VectorMath.add(viewportUpperLeft, VectorMath.scale(deltaU, 0.5), VectorMath.scale(deltaV, 0.5));
        calculateRays();
    }

    public void calculateRays() //TODO partial precomputation for efficiency
    {
        int l = MainController.getPixelsLength();
        rays = new double[l*3];
        for (int i = 0; i < l; i++) {
            int x = i%Application.WIDTH, y = i/Application.WIDTH;
            double[] ray = VectorMath.subtract(VectorMath.add(upperLeftPixel, VectorMath.scale(deltaU, x), VectorMath.scale(deltaV, y)), pos).toArray();
            rays[3*i] = ray[0];
            rays[3*i+1] = ray[1];
            rays[3*i+2] = ray[2];
        }

        precalculation();
    }

    public void precalculation()
    {
        kIdxs = new int[rays.length];
        shearFactors = new double[rays.length];

        for (int i = 0; i < rays.length/3; i++) {
            double[] absray = new double[]{Math.abs(rays[i*3]), Math.abs(rays[i*3+1]), Math.abs(rays[i*3+2])};
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
            shearFactors[i*3+2] = 1D/rays[i*3 + kz];             //Sz
        }
    }

    public double[] getRays()
    {
        return rays;
    }

    public int[] getkIdxs() {
        return kIdxs;
    }

    public double[] getShearFactors() {
        return shearFactors;
    }
}
