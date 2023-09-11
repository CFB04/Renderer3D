package cfbastian.renderer3d;

import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;

public class Camera {
    private Vector3 pos, dir;
    private double focalLength, fov, viewportHeight, viewportWidth, halfViewportDiagonal;
    private double uSpacing, vSpacing;
    private Vector3 deltaU, deltaV;
    private Vector3 viewportCenter, viewportUpperLeft, upperLeftPixel; // Center is not actually rendered when either width or height is odd
    private double theta, phi; // Theta is angle off the positive x-axis, phi is angle off the (vertical) positive z-axis
    private double[] rays;

    public Camera(Vector3 pos, double theta, double phi, double fov) {
        this.pos = pos;
        this.theta = theta;
        this.phi = phi;
        this.fov = fov;

        this.viewportWidth = 2.0;
        this.viewportHeight = viewportWidth * (double) Application.HEIGHT / (double) Application.WIDTH;
        this.halfViewportDiagonal = Math.sqrt(viewportWidth*viewportWidth + viewportHeight*viewportHeight)/2D;

        this.dir = new Vector3(Math.sin(phi)*Math.cos(theta), Math.sin(phi)*Math.sin(theta), Math.cos(phi));
        this.focalLength = 1D; //2D * viewportWidth / (Math.tan(Math.PI * this.fov / 90D)); //TODO change back
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

    public void calculateRays()
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
    }

    public double[] getRays()
    {
        return rays;
    }
}
