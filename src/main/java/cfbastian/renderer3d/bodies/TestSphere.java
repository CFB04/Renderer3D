package cfbastian.renderer3d.bodies;

import cfbastian.renderer3d.Renderer;

public class TestSphere {

    public double[] pos;
    public double radius;

    public TestSphere(double[] pos, double r) {
        this.pos = pos;
        this.radius = r;
    }

    public double getT(double[] cameraPos, double[] ray)
    {
        double[] oc = new double[]{cameraPos[0] - pos[0], cameraPos[1] - pos[1], cameraPos[2] - pos[2]};
        double a = ray[0]*ray[0] + ray[1]*ray[1] + ray[2]*ray[2], halfb = (oc[0]*ray[0] + oc[1]*ray[1] + oc[2]*ray[2]), c = oc[0]*oc[0] + oc[1]*oc[1] + oc[2]*oc[2] - radius*radius;
        double discriminant = halfb*halfb - a*c;
        if(discriminant < 0D) return -1D;
        else return (-halfb + Math.sqrt(discriminant)) / a;
    }
}
