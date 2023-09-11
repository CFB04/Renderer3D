package cfbastian.renderer3d;

import cfbastian.renderer3d.bodies.Mesh;
import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.VectorMath;
import cfbastian.renderer3d.util.ObjFileManager;

import java.io.IOException;
import java.util.Arrays;

public class Renderer {
    private final int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    private Scene mainScene;
    double elapsedTime;

    double[] rays;
    double[] cameraPos;

    public void initScene()
    {
        mainScene = new Scene();
        try {
            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/Quad.obj", new double[]{4D, 0D, 0D}, 1D, 2, "Quad"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mesh cube = mainScene.getMesh("Quad");
//        System.out.println(Arrays.toString(cube.getVertices()));
//        System.out.println(Arrays.toString(cube.getTextureCoords()));
//        System.out.println(Arrays.toString(cube.getVertexNormals()));
//        System.out.println(Arrays.toString(cube.getFaces()));
//        System.out.println(Arrays.toString(cube.getUvs()));
//        System.out.println(Arrays.toString(cube.getNormals()));
    }

    public void updateScene(double elapsedTime)
    {

    }

    public int[] render(double elapsedTime, double[] rays, double[] cameraPos)
    {
        this.elapsedTime = elapsedTime;
        this.rays = rays;
        this.cameraPos = cameraPos;

        Mesh quad = mainScene.getMesh("Quad");

//        System.out.println(Arrays.toString(quad.getAbsoluteVertices()));

        for (int i = 0; i < pixels.length; i++) pixels[i] = getPixel(i, cameraPos, rays, quad.getAbsoluteVertices(), quad.getFaceNormals(), quad.getFaces(), quad.getNormals()); //TODO instead of passing in the whole scene for rendering, optimize by passing in subsets (only visible entities, oct tress)
        return pixels;
    }

    private int getPixel(int i, double[] pos, double[] rays, double[] vertices, double[] normals, int[] faces, int[] normalIndices)
    {
        double[] ray = Arrays.copyOfRange(rays, i*3, i*3+3);

        double[] col = new double[]{0D, 0D, 0D};

        double[] ts = new double[faces.length/3];

        for (int j = 0; j < faces.length/3; j++) {
            ts[j] = hitTri(
                    new double[]{vertices[faces[j*3]*3], vertices[faces[j*3]*3+1], vertices[faces[j*3]*3+2]},
                    new double[]{vertices[faces[j*3+1]*3], vertices[faces[j*3+1]*3+1], vertices[faces[j*3+1]*3+2]},
                    new double[]{vertices[faces[j*3+2]*3], vertices[faces[j*3+2]*3+1], vertices[faces[j*3+2]*3+2]},
                    new double[]{normals[normalIndices[j*3]*3], normals[normalIndices[j*3+1]*3+1], normals[normalIndices[j*3+2]*3+2]}, pos, ray, i);
            ts[j] = ts[j] > 0 ? ts[j] : Double.MAX_VALUE;
        }
        double t = ScalarMath.min(ts);

        if(t > 0D && t != Double.MAX_VALUE) col = new double[]{0.0, 0.0, 1.0};
        else if(t == Double.MAX_VALUE)
        {
            // Background
            double rayDirLength = Math.sqrt(ray[0]*ray[0] + ray[1]*ray[1] + ray[2]*ray[2]);
            double[] unitDir = new double[]{ray[0]/rayDirLength, ray[1]/rayDirLength, ray[2]/rayDirLength};
            double a = 0.5 * (unitDir[2] + 1D);

            col = new double[]{(1D - a) * 1 + a * 0.5, (1D - a) * 1 + a * 0.7, (1D - a) * 1 + a};
        }

        boundColor(col);
        int[] color = new int[]{(int) (col[0] * 255), (int) (col[1] * 255), (int) (col[2] * 255)};
        return 0xFF000000 + (color[0]<<16) + (color[1]<<8) + color[2];
    }

    public double hitTri(double[] v0, double[] v1, double[] v2, double[] N, double[] pos, double[] ray, int i)
    {
        double NdotR = N[0]*ray[0] + N[1]*ray[1] + N[2]*ray[2];
        if(Math.abs(NdotR) < Constants.K_EPSILON) return -1D;
        else if(NdotR > 0) return -1D;

        double NdotPos = (N[0]*pos[0] + N[1]*pos[1] + N[2]*pos[2]);
        double D = N[0]*v0[0] + N[1]*v0[1] + N[2]*v0[2];

        double t = (D - NdotPos) / NdotR;

        double[] P = new double[]{pos[0] + ray[0] * t, pos[1] + ray[1] * t, pos[2] + ray[2] * t};
        double[] edge = new double[]{v1[0]-v0[0], v1[1]-v0[1], v1[2]-v0[2]};
        double[] vp = new double[]{P[0]-v0[0], P[1]-v0[1], P[2]-v0[2]};
        double[] C = VectorMath.cross(edge, vp);
        if(C[0]*N[0] + C[1]*N[1] + C[2]*N[2] < 0D) return -1D;
        edge = new double[]{v2[0]-v1[0], v2[1]-v1[1], v2[2]-v1[2]};
        vp = new double[]{P[0]-v1[0], P[1]-v1[1], P[2]-v1[2]};
        C = VectorMath.cross(edge, vp);
        if(C[0]*N[0] + C[1]*N[1] + C[2]*N[2] < 0D) return -1D;
        edge = new double[]{v0[0]-v2[0], v0[1]-v2[1], v0[2]-v2[2]};
        vp = new double[]{P[0]-v2[0], P[1]-v2[1], P[2]-v2[2]};
        C = VectorMath.cross(edge, vp);
        if(C[0]*N[0] + C[1]*N[1] + C[2]*N[2] < 0D) return -1D;

        System.out.println(t);
        return t;
    }

    private void boundColor(double[] color)
    {
        color[0] = Math.min(color[0], 1D);
        color[1] = Math.min(color[1], 1D);
        color[2] = Math.min(color[2], 1D);
        color[0] = Math.max(color[0], 0D);
        color[1] = Math.max(color[1], 0D);
        color[2] = Math.max(color[2], 0D);
    }
}
