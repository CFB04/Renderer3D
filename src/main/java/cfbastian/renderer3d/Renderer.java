package cfbastian.renderer3d;

public class Renderer {
    public static final double MAX_DIST = 100D, MIN_DIST  = 0.01D, NORMAL_DIST = 0.005D;
    public static final int MAX_STEPS = 200;

    private int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    private Scene mainScene = new Scene();

    double[] origin = new double[]{0D, 1D, 0D};
    double[] direction = new double[pixels.length * 3];

    public void initScene()
    {

    }

    public void updateScene(double elapsedTime)
    {

    }

    private int getPixel(int i, double elapsedTime, Scene renderScene)
    {
        return 0xFF000000;
    }

    public int[] render(double elapsedTime)
    {
        for (int i = 0; i < pixels.length; i++) pixels[i] = getPixel(i, elapsedTime, mainScene); //TODO instead of passing in the whole scene for rendering, optimize by passing in subsets (only visible entities, oct tress)
        return pixels;
    }
}
