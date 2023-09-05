package cfbastian.renderer3d;

import cfbastian.renderer3d.entities.Entity;
import cfbastian.renderer3d.entities.HorizontalPlane;
import cfbastian.renderer3d.entities.Sphere;
import cfbastian.renderer3d.entities.lights.Light;
import cfbastian.renderer3d.entities.lights.PointLight;
import cfbastian.renderer3d.math.*;

import java.util.ArrayList;

public class Renderer {
    public static final double MAX_DIST = 100D, MIN_DIST  = 0.01D, NORMAL_DIST = 0.005D;
    public static final int MAX_STEPS = 200;

    private int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    private Scene mainScene = new Scene();

    public void initScene()
    {
        mainScene.addToEntityList(new HorizontalPlane(new Vector3(0D, 0D, 0D), "ground"));
        mainScene.addToEntityList(new Sphere(new Vector3(0D, 1D, 7D), 1, "ball"));
        mainScene.addToLightList(new PointLight(new Vector3(0D + 3, 4D, 7D), 20D, "circlingLight"));
    }

    public void updateScene(double elapsedTime)
    {
        int index = mainScene.findLightIndex("circlingLight");
        mainScene.getFromLightList(index).setPos(new Vector3(0D + 3 * Math.cos(elapsedTime), 4D, 7D + 3 * Math.sin(elapsedTime)));

        //System.out.println(Arrays.toString(mainScene.getKeys()));
    }

    private double getDistToScene(ArrayList<Entity> entityList, Vector3 rayOrigin)
    {
        double[] dists = new double[entityList.size()];
        for (int k = 0; k < entityList.size(); k++) dists[k] = entityList.get(k).getDistance(rayOrigin);
        return ScalarMath.min(dists);
    }

    public static final Vector3[] NORMAL_MATRIX = new Vector3[]{
            new Vector3(NORMAL_DIST, 0D, 0D),
            new Vector3(0D, NORMAL_DIST, 0D),
            new Vector3(0D, 0D, NORMAL_DIST)
    };
    private Vector3 getNormal(ArrayList<Entity> entityList, Vector3 pos)
    {
        double d = getDistToScene(entityList, pos);
        Vector3 v = VectorMath.normalize(new Vector3(d - getDistToScene(entityList, VectorMath.subtract(pos, NORMAL_MATRIX[0])), d - getDistToScene(entityList, VectorMath.subtract(pos, NORMAL_MATRIX[1])), d - getDistToScene(entityList, VectorMath.subtract(pos, NORMAL_MATRIX[2]))));

        return VectorMath.normalize(new Vector3(d - getDistToScene(entityList, VectorMath.subtract(pos, NORMAL_MATRIX[0])), d - getDistToScene(entityList, VectorMath.subtract(pos, NORMAL_MATRIX[1])), d - getDistToScene(entityList, VectorMath.subtract(pos, NORMAL_MATRIX[2]))));
    }

    private int getPixel(int i, double elapsedTime, Scene renderScene)
    {
        Vector3 color;

        Vector2 uv = indexToUV(i);

        Vector3 rayOrigin = new Vector3(0, 1, 0);
        Vector3 rayDirection = VectorMath.normalize(new Vector3(uv.x, uv.y, 1));
        double rayDist = 0D;

        for (int j = 0; j < MAX_STEPS; j++) {
            double distance = 0D;

            distance = getDistToScene(renderScene.getEntityList(), rayOrigin);
            rayDist += distance;
            if(distance < MIN_DIST || rayDist > MAX_DIST) break;
            rayOrigin = VectorMath.add(rayOrigin, VectorMath.scale(rayDirection, distance));
        }

        double brightness = 0.1D;

        for (Light light : renderScene.getLightList()) brightness += ScalarMath.boundMin(light.getLight(rayOrigin, getNormal(renderScene.getEntityList(), rayOrigin)), 0D);
        if(rayDist > MAX_DIST) brightness = 1.0D;

        color = new Vector3(brightness, brightness, brightness);
//        color = getNormal(renderScene.getEntityList(), rayOrigin);

        int ir = (int) (color.x * 255D), ig = (int) (color.y * 255D), ib = (int) (color.z * 255D);
        ir = ScalarMath.bound(ir, 0, 255);
        ig = ScalarMath.bound(ig, 0, 255);
        ib = ScalarMath.bound(ib, 0, 255);
        return 0xFF000000 + (ir << 16) + (ig << 8) + ib;
    }

    public int[] render(double elapsedTime)
    {
        for (int i = 0; i < pixels.length; i++) pixels[i] = getPixel(i, elapsedTime, mainScene); //TODO instead of passing in the whole scene for rendering, optimize by passing in subsets (only visible entities, oct tress)
        return pixels;
    }

    public IntVector2 indexToCoords(int index)
    {
        return new IntVector2(index % Application.WIDTH, index / Application.WIDTH);
    }

    public Vector2 indexToUV(int index)
    {
        IntVector2 coords = indexToCoords(index);
        return new Vector2(((double) coords.x - 0.5 * Application.WIDTH) / (double) Application.HEIGHT, -(((double) coords.y - 0.5 * Application.HEIGHT) / (double) Application.HEIGHT));
    }

    public Vector2 coordsToUV(IntVector2 coords)
    {
        return new Vector2(((double) coords.x - 0.5 * Application.WIDTH) / (double) Application.HEIGHT, -(((double) coords.y - 0.5 * Application.HEIGHT) / (double) Application.HEIGHT));
    }
}
