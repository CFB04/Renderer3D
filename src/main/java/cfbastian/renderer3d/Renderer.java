package cfbastian.renderer3d;

import cfbastian.renderer3d.bodies.Mesh;
import cfbastian.renderer3d.bodies.TestSphere;
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

    double[] sphereCoords, sphereRadii;

    public void initScene()
    {
        mainScene = new Scene();

        mainScene.addSphere(new TestSphere(new double[]{2D, -1D, 0D}, 0.5));
        mainScene.addSphere(new TestSphere(new double[]{2D, 1D, 0D}, 0.5));

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

        mainScene.calculateSphereArrays();
        sphereCoords = mainScene.getSpheresCoords();
        sphereRadii = mainScene.getSpheresRadii();
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

        for (int i = 0; i < pixels.length; i++) pixels[i] = getPixel(i, cameraPos, rays, quad.getAbsoluteVertices(), quad.getFaceNormals(), quad.getFaces(), quad.getNormals(), mainScene.spheres.size()); //TODO instead of passing in the whole scene for rendering, optimize by passing in subsets (only visible entities, oct tress)
        return pixels;
    }

    private int getPixel(int i, double[] pos, double[] rays, double[] vertices, double[] normals, int[] faces, int[] normalIndices, int numSpheres)
    {
//        if(i%(Application.WIDTH*Application.HEIGHT/20) == 0) System.out.println((double) i /(double) pixels.length);
        double[] ray = Arrays.copyOfRange(rays, i*3, i*3+3);

        double[] col = new double[]{0D, 0D, 0D};

//        double[] ts = new double[numSpheres];
//        for (int j = 0; j < numSpheres; j++) {
//            double t = hitSphere(Arrays.copyOfRange(sphereCoords, j*3, j*3+3), sphereRadii[j], pos, ray);
//            ts[j] = t > 0 ? t : Double.MAX_VALUE;
//        }
        double[] ts = new double[faces.length/3];
//        if(i == 1){
//        System.out.println(Arrays.toString(vertices));
//        System.out.println(Arrays.toString(normals));
//        System.out.println(Arrays.toString(faces));
//        System.out.println(faces.length/3);

        for (int j = 0; j < faces.length/3; j++) {
//            System.out.println(Arrays.toString(new double[]{vertices[faces[j * 3]*3], vertices[faces[j * 3]*3 + 1], vertices[faces[j * 3]*3 + 2]}));
//            System.out.println(Arrays.toString(new double[]{vertices[faces[j * 3+1]*3], vertices[faces[j * 3+1]*3 + 1], vertices[faces[j * 3+1]*3 + 2]}));
//            System.out.println(Arrays.toString(new double[]{vertices[faces[j * 3+2]*3], vertices[faces[j * 3+2]*3 + 1], vertices[faces[j * 3+2]*3 + 2]}));
            ts[j] = hitTri(
                    new double[]{vertices[faces[j*3]*3], vertices[faces[j*3]*3+1], vertices[faces[j*3]*3+2]},
                    new double[]{vertices[faces[j*3+1]*3], vertices[faces[j*3+1]*3+1], vertices[faces[j*3+1]*3+2]},
                    new double[]{vertices[faces[j*3+2]*3], vertices[faces[j*3+2]*3+1], vertices[faces[j*3+2]*3+2]},
                    new double[]{normals[normalIndices[j*3]*3], normals[normalIndices[j*3+1]*3+1], normals[normalIndices[j*3+2]*3+2]}, pos, ray, i);
            ts[j] = ts[j] > 0 ? ts[j] : Double.MAX_VALUE;
        }
//        }
        double t = ScalarMath.min(ts);
//        t = Double.MAX_VALUE;

//        double tTemp = hitTri(new double[]{3D, 1D, 0D}, new double[]{3D, -1D, 0D}, new double[]{3D, 0D, 2D}, new double[]{-1D, 0D, 0D}, pos, ray, i);
//
//        if(tTemp > 0D)
//        {
//            col = new double[]{1.0, 0.0, 0.0};
//        }

        if(t > 0D && t != Double.MAX_VALUE)
        {
//            double[] hit = new double[]{pos[0] + ray[0] * t, pos[1] + ray[1] * t, pos[2] + ray[2] * t};
//            double[] N = new double[]{hit[0], hit[1], hit[2] + 1};
//            double Nl = Math.sqrt(N[0]*N[0] + N[1]*N[1] + N[2]*N[2]);
//            N[0] /= Nl; N[1] /= Nl; N[2] /= Nl;
//            col = new double[]{0.5 * (N[0] + 1), 0.5 * (N[1] + 1), 0.5 * (N[2] + 1)};
            col = new double[]{0.0, 0.0, 1.0};
        }
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

    public double hitSphere(double[] center, double radius, double[] pos, double[] ray)
    {
        double[] oc = new double[]{pos[0] - center[0], pos[1] - center[1], pos[2] - center[2]};
        double a = ray[0]*ray[0] + ray[1]*ray[1] + ray[2]*ray[2];
        double halfb = (oc[0]*ray[0] + oc[1]*ray[1] + oc[2]*ray[2]);
        double c = oc[0]*oc[0] + oc[1]*oc[1] + oc[2]*oc[2] - radius*radius;
        double discriminant = halfb*halfb - a*c;
        if(discriminant < 0D) return -1D;
        else return (-halfb + Math.sqrt(discriminant)) / a;
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
