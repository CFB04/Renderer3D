package cfbastian.renderer3d.compute;

import cfbastian.renderer3d.Application;
import cfbastian.renderer3d.Scene;
import cfbastian.renderer3d.entities.Entity;
import cfbastian.renderer3d.entities.HorizontalPlane;
import cfbastian.renderer3d.entities.Sphere;
import cfbastian.renderer3d.entities.lights.Light;
import cfbastian.renderer3d.entities.lights.PointLight;
import cfbastian.renderer3d.math.*;
import com.aparapi.Kernel;
import com.aparapi.Range;

import java.util.ArrayList;
import java.util.Arrays;

public class ComputeRenderer {
    public static final double MAX_DIST = 100D, MIN_DIST  = 0.01D, NORMAL_DIST = 0.005D;
    public static final int MAX_STEPS = 200;
    public static final float W = (float) Application.WIDTH, H = (float) Application.HEIGHT;

    private int[] pixels = new int[Application.WIDTH * Application.HEIGHT];
    Range range = Range.create(pixels.length);

    RenderKernel renderKernel = new RenderKernel();

    private Scene mainScene = new Scene();

    float[] cameraPos;
    float[][] rayDirections = new float[pixels.length][3];

    Entity[] entityArray;
    Light[] lightArray;

    public void init()
    {
        mainScene.addToEntityList(new HorizontalPlane(new Vector3(0D, 0D, 0D), "ground"));
        mainScene.addToEntityList(new Sphere(new Vector3(0D, 1D, 7D), 1D, "ball"));
        mainScene.addToLightList(new PointLight(new Vector3(0D + 3, 4D, 7D), 20D, "circlingLight"));

        cameraPos = new float[]{0f, 1f, 0f};
    }

    public void update(double elapsedTime)
    {
        int index = mainScene.findLightIndex("circlingLight");
        mainScene.getFromLightList(index).setPos(new Vector3(0D + 3 * Math.cos(elapsedTime), 4D, 7D + 3 * Math.sin(elapsedTime)));

        for (int i = 0; i < pixels.length; i++) {
            int x = i % Application.WIDTH, y = i / Application.WIDTH;
            float a = ((float) x - 0.5f * W) / H,
                    b = -((float) y - 0.5f * H) / H;
            float l = (float) Math.sqrt(a*a + b*b + 1f);
            rayDirections[i] = new float[]{a/l, b/l, 1f/l};
        }

        entityArray = new Entity[mainScene.getEntityList().size()];
        entityArray = mainScene.getEntityList().toArray(entityArray);
        lightArray = new Light[mainScene.getLightList().size()];
        lightArray = mainScene.getLightList().toArray(lightArray);

        renderKernel.update();
    }

    public int[] render(double elapsedTime)
    {
        renderKernel.execute(range);
        return pixels;
    }

    private class RenderKernel extends Kernel
    {
        @Local float[][] rayDirs;
        @Local float[] rayOrigin;
        @Local Entity[] entityArr;

        private synchronized void update()
        {
            rayDirs = rayDirections;
            rayOrigin = cameraPos;
            entityArr = Arrays.copyOf(entityArray, entityArray.length);
        }

        @Override
        public void run() {
            int index = getGlobalId();
            float rayDist = 0f;
            float[] rayPos = rayOrigin;

            for (int i = 0; i < MAX_STEPS; i++) {
                float[] dists = new float[entityArr.length];

                for (int j = 0; j < dists.length; j++) dists[j] = (float) entityArr[j].getDistance(rayPos);
                float distance = Float.MAX_VALUE;
                for (int j = 0; j < dists.length; j++) distance = Math.min(distance, dists[j]);

                rayDist += distance;
                if(distance < MIN_DIST || rayDist > MAX_DIST) break;
//                rayPos[0] += rayDirs[index][0] * distance;
//                rayPos[1] += rayDirs[index][1] * distance;
//                rayPos[2] += rayDirs[index][2] * distance;
                float x = rayPos[0] + rayDirs[index][0] * distance;
                float y = rayPos[1] + rayDirs[index][1] * distance;
                float z = rayPos[2] + rayDirs[index][2] * distance;
//                rayPos[0] = x;
//                rayPos[1] = y;
//                rayPos[2] = z;
                rayPos = new float[]{x, y, z};
            }

            //System.out.println(rayDist);

            int c = (int) (255f * rayDist / 10f);
            c = Math.max(c, 0);
            c = Math.min(c, 255);
            pixels[index] = 0xFF000000 + (c<<16) + (c<<8) + c;
        }
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

    private double getDistToScene(ArrayList<Entity> entityList, Vector3 rayOrigin)
    {
        double[] dists = new double[entityList.size()];
        for (int k = 0; k < entityList.size(); k++) dists[k] = entityList.get(k).getDistance(rayOrigin);
        return ScalarMath.min(dists);
    }
}
