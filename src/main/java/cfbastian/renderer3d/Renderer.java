package cfbastian.renderer3d;

import cfbastian.renderer3d.math.Vector3;

import java.util.Arrays;

public class Renderer {
    private int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    private Scene mainScene = new Scene();
    private Camera camera = new Camera(new Vector3(0D, 0D, 0D), 0D, Math.PI/2D, 90D);

    double[] origin = new double[]{0D, 0D, 0D};
    double[] direction = new double[pixels.length * 3];
    double focalLength = 1D, viewportHeight = 2D, viewportWidth = viewportHeight * (double) Application.WIDTH / (double) Application.HEIGHT;

    double pixelDeltaU = viewportWidth/(double) Application.WIDTH, pixelDeltaV = - viewportHeight/(double) Application.HEIGHT;

    double[] viewportUpperLeft = new double[]{origin[0] - viewportWidth/2D, origin[1] + viewportHeight/2D, origin[2] - focalLength};
    double[] pixel00Loc = new double[]{viewportUpperLeft[0] + 0.5 * pixelDeltaU, viewportUpperLeft[1] + 0.5 * pixelDeltaV, viewportUpperLeft[2]};

    public void initScene()
    {
        System.out.println(Arrays.toString(camera.getDir().toArray()));
        System.out.println(Arrays.toString(camera.getViewportCenter().toArray()));
        System.out.println(Arrays.toString(camera.getViewportUpperLeft().toArray()));
    }

    public void updateScene(double elapsedTime)
    {
        //camera.setPhi(elapsedTime/2D);
    }

    public void ChangeCameraAngle(double mouseDX, double mouseDY)
    {
        camera.setTheta(camera.getTheta() - mouseDX/100D);
        camera.setPhi(camera.getPhi() + mouseDY/100D);
        System.out.println(mouseDX + "\t" + mouseDY);
        System.out.println("Angles: " + camera.getTheta() + "\t" + camera.getPhi());
//        System.out.println(Arrays.toString(camera.getDir().toArray()));
    }

    private int getPixel2(int i, double elapsedTime, Scene renderScene)
    {

        return 0xFF000000;
    }

    private int getPixel(int i, double elapsedTime, Scene renderScene)
    {
        int x = i%Application.WIDTH, y = i/Application.WIDTH;
        double[] pixelcenter = new double[]{pixel00Loc[0] + x * pixelDeltaU, pixel00Loc[1] + y * pixelDeltaV, pixel00Loc[2]};
//        double[] rayDir = new double[]{pixelcenter[0] - origin[0], pixelcenter[1] - origin[1], pixelcenter[2] - origin[2]};
        double[] rayDir = Arrays.copyOfRange(camera.getRays(), i*3, i*3+3);

        Ray r = new Ray(origin, rayDir);

        // Background
        double rayDirLength = Math.sqrt(r.direction[0]*r.direction[0] + r.direction[1]*r.direction[1] + r.direction[2]*r.direction[2]);
        double[] unitDir = new double[]{r.direction[0]/rayDirLength, r.direction[1]/rayDirLength, r.direction[2]/rayDirLength};
        double a = 0.5 * (unitDir[2] + 1D);
        //

        double[] col = new double[]{(1D - a) * 1 + a * 0.5, (1D - a) * 1 + a * 0.7, (1D - a) * 1 + a};

        double t = hitSphere(new double[]{1D, 0D, 0D}, 0.5, r);
        if(t > 0D)
        {
            double[] N = new double[]{r.at(t)[0], r.at(t)[1], r.at(t)[2] + 1};
            double Nl = Math.sqrt(N[0]*N[0] + N[1]*N[1] + N[2]*N[2]);
            N[0] /= Nl; N[1] /= Nl; N[2] /= Nl;
            col = new double[]{0.5 * (N[0] + 1), 0.5 * (N[1] + 1), 0.5 * (N[2] + 1)};
        }

        col[0] = Math.min(col[0], 1D); col[1] = Math.min(col[1], 1D); col[2] = Math.min(col[2], 1D);
        col[0] = Math.max(col[0], 0D); col[1] = Math.max(col[1], 0D); col[2] = Math.max(col[2], 0D);
        int[] color = new int[]{(int) (col[0] * 255), (int) (col[1] * 255), (int) (col[2] * 255)};
        return 0xFF000000 + (color[0]<<16) + (color[1]<<8) + color[2];
    }

    public double hitSphere(double[] center, double radius, Ray r)
    {
        double[] oc = new double[]{r.origin[0] - center[0], r.origin[1] - center[1], r.origin[2] - center[2]};
        double a = r.directionLengthSqrd, halfb = (oc[0]*r.direction[0] + oc[1]*r.direction[1] + oc[2]*r.direction[2]), c = oc[0]*oc[0] + oc[1]*oc[1] + oc[2]*oc[2] - radius*radius;
        double discriminant = halfb*halfb - a*c;
        if(discriminant < 0D) return -1D;
        else return (-halfb + Math.sqrt(discriminant)) / a;
    }

    public int[] render(double elapsedTime)
    {
        for (int i = 0; i < pixels.length; i++) pixels[i] = getPixel(i, elapsedTime, mainScene); //TODO instead of passing in the whole scene for rendering, optimize by passing in subsets (only visible entities, oct tress)
        return pixels;
    }

    public class Ray
    {
        public double[] origin, direction;
        public double directionLengthSqrd;

        public Ray(double[] origin, double[] direction) {
            this.origin = origin;
            this.direction = direction;
            this.directionLengthSqrd = this.direction[0]*this.direction[0] + this.direction[1]*this.direction[1] + this.direction[2]*this.direction[2];
        }

        public double[] at(double t)
        {
            return new double[]{origin[0] + direction[0] * t, origin[1] + direction[1] * t, origin[2] + direction[2] * t};
        }
    }
}
