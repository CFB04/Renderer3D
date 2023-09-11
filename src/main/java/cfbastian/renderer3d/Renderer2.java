package cfbastian.renderer3d;

import cfbastian.renderer3d.bodies.TestSphere;
import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.Vector3;

import java.util.Arrays;

public class Renderer2 {
    private int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    private Scene mainScene = new Scene();
    private Camera camera = new Camera(new Vector3(0D, 0D, 0D), 0D, Math.PI/2D, 90D);

    double[] sphereCoords;
    double[] sphereRadii;

    double[] rays;

    public void initScene()
    {
        mainScene.addSphere(new TestSphere(new double[]{1D, 0D, 0D}, 0.5));
        mainScene.calculateSphereArrays();

        sphereCoords = mainScene.getSpheresCoords();
        sphereRadii = mainScene.spheresRadii;
    }

    public void updateScene(double elapsedTime)
    {
        rays = camera.getRays();
    }

    private int getPixel(int i, double elapsedTime, Scene renderScene)
    {
        double[] rayDir = Arrays.copyOfRange(camera.rays, i*3, i*3+3);

        double[] ts = new double[mainScene.spheres.size()];
        for (int j = 0; j < mainScene.spheres.size(); j++) {
            double[] oc = new double[]{camera.getPos().x - sphereCoords[j*3], camera.getPos().y - sphereCoords[j*3+1], camera.getPos().z - sphereCoords[j*3+2]};
            double a = rays[j*3]*rays[j*3] + rays[j*3+1]*rays[j*3+1] + rays[j*3+2]*rays[j*3+2];
            double halfb = (oc[0]*rays[j*3] + oc[1]*rays[j*3+1] + oc[2]*rays[j*3+2]);
            double c = oc[0]*oc[0] + oc[1]*oc[1] + oc[2]*oc[2] - sphereRadii[j]*sphereRadii[j];
            double discriminant = halfb*halfb - a*c;

            if(discriminant < 0D) ts[j] = Double.MAX_VALUE;
            else ts[j] = (-halfb + Math.sqrt(discriminant)) / a;
        }

        double mint = ScalarMath.min(ts);
        if(mint != Double.MAX_VALUE) return 0xFFFF0000;

        return 0xFF000000;
    }

    public int[] render(double elapsedTime)
    {
        for (int i = 0; i < pixels.length; i++) pixels[i] = getPixel(i, elapsedTime, mainScene); //TODO instead of passing in the whole scene for rendering, optimize by passing in subsets (only visible entities, oct tress)
        return pixels;
    }
}
