package cfbastian.renderer3d;

import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.Vector3;

import java.util.Arrays;

public class Renderer {
    private int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    private Scene mainScene = new Scene();
    private Camera camera = new Camera(new Vector3(0D, 0D, 0D), 0D, Math.PI/2D, 90D);

    double[] rays;

    public void initScene()
    {

    }

    public void updateScene(double elapsedTime)
    {

    }

    public void ChangeCameraAngle(double mouseDX, double mouseDY)
    {
        camera.setAngle(camera.getTheta() - mouseDX/100D, camera.getPhi() + mouseDY/100D);
    }

    public int[] render(double elapsedTime)
    {
        rays = camera.getRays();
        for (int i = 0; i < pixels.length; i++) pixels[i] = getPixel(i, elapsedTime, mainScene); //TODO instead of passing in the whole scene for rendering, optimize by passing in subsets (only visible entities, oct tress)
        return pixels;
    }

    private int getPixel(int i, double elapsedTime, Scene renderScene)
    {
        double[] ray = Arrays.copyOfRange(rays, i*3, i*3+3);
        double[] pos = camera.getPos().toArray();

        // Background
        double rayDirLength = Math.sqrt(ray[0]*ray[0] + ray[1]*ray[1] + ray[2]*ray[2]);
        double[] unitDir = new double[]{ray[0]/rayDirLength, ray[1]/rayDirLength, ray[2]/rayDirLength};
        double a = 0.5 * (unitDir[2] + 1D);

        double[] col = new double[]{(1D - a) * 1 + a * 0.5, (1D - a) * 1 + a * 0.7, (1D - a) * 1 + a};

        double t = hitSphere(new double[]{1D, 0D, 0D}, 0.5, ray);
        if(t > 0D)
        {
            double[] hit = new double[]{pos[0] + ray[0] * t, pos[1] + ray[1] * t, pos[2] + ray[2] * t};
            double[] N = new double[]{hit[0], hit[1], hit[2] + 1};
            double Nl = Math.sqrt(N[0]*N[0] + N[1]*N[1] + N[2]*N[2]);
            N[0] /= Nl; N[1] /= Nl; N[2] /= Nl;
            col = new double[]{0.5 * (N[0] + 1), 0.5 * (N[1] + 1), 0.5 * (N[2] + 1)};
        }

        boundColor(col);
        int[] color = new int[]{(int) (col[0] * 255), (int) (col[1] * 255), (int) (col[2] * 255)};
        return 0xFF000000 + (color[0]<<16) + (color[1]<<8) + color[2];
    }

    public double hitSphere(double[] center, double radius, double[] ray)
    {
        double[] oc = new double[]{camera.getPos().x - center[0], camera.getPos().y - center[1], camera.getPos().z - center[2]};
        double a = ray[0]*ray[0] + ray[1]*ray[1] + ray[2]*ray[2];
        double halfb = (oc[0]*ray[0] + oc[1]*ray[1] + oc[2]*ray[2]);
        double c = oc[0]*oc[0] + oc[1]*oc[1] + oc[2]*oc[2] - radius*radius;
        double discriminant = halfb*halfb - a*c;
        if(discriminant < 0D) return -1D;
        else return (-halfb + Math.sqrt(discriminant)) / a;
    }

    private double[] boundColor(double[] color)
    {
        color[0] = Math.min(color[0], 1D);
        color[1] = Math.min(color[1], 1D);
        color[2] = Math.min(color[2], 1D);
        color[0] = Math.max(color[0], 0D);
        color[1] = Math.max(color[1], 0D);
        color[2] = Math.max(color[2], 0D);
        return color;
    }
}
