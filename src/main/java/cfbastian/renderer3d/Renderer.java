package cfbastian.renderer3d;

import cfbastian.renderer3d.util.ObjFileManager;
import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;

import java.io.IOException;
import java.util.Arrays;

public class Renderer {
    private final int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    private Scene mainScene;
    double elapsedTime;

    double[] rays, vertices;
    int[] faces;
    int[] kIdxs;
    double[] shearFactors;
    double[] cameraPos;

    RenderKernel renderKernel = new RenderKernel();
    Range range;

    public void init()
    {
        range = Range.create(pixels.length, 320);

        mainScene = new Scene();
        try {
//            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/StanfordBunny.obj", new double[]{6D, 0D, -2D}, 5D, 2, "Quad1"));
            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/Quad.obj", new double[]{4.5, 0D, 0D}, 1D, 2, "Quad2"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vertices = mainScene.getAllVertices();
        faces = mainScene.getAllFaces();
    }

    public int[] render(double elapsedTime, double[] rays, int[] kIdxs, double[] shearFactors, double[] cameraPos)
    {
        this.elapsedTime = elapsedTime;
        this.rays = rays;
        this.kIdxs = kIdxs;
        this.shearFactors = shearFactors;
        this.cameraPos = cameraPos;

        renderKernel.update(cameraPos, rays, kIdxs, shearFactors, vertices, faces);
        renderKernel.execute(range);

        return pixels;
    }

    /**
     * Gets the color for the pixel at the given index. Ray-Triangle intersection algorithm from
     * Watertight Ray/Triangle Intersection by Woop et al. TODO fix citation
     * */
    private int getPixel(int i, double[] pos, double[] rays, int[] kIdxs, double[] shearFactors, double[] vertices, int[] faces)
    {
        double[] ray = Arrays.copyOfRange(rays, i*3, i*3+3);
        double[] col;

        double[] A, B, C;
        int kx = kIdxs[i*3], ky = kIdxs[i*3+1], kz = kIdxs[i*3+2];
        double Sx = shearFactors[i*3], Sy = shearFactors[i*3+1], Sz = shearFactors[i*3+2];

        double t = Double.MAX_VALUE, u = Double.MAX_VALUE, v = Double.MAX_VALUE, w = Double.MAX_VALUE;

        for (int j = 0; j < faces.length/3; j++) {
            A = new double[]{vertices[faces[j*3]*3] - pos[0], vertices[faces[j*3]*3+1] - pos[1], vertices[faces[j*3]*3+2] - pos[2]};
            B = new double[]{vertices[faces[j*3+1]*3] - pos[0], vertices[faces[j*3+1]*3+1] - pos[1], vertices[faces[j*3+1]*3+2] - pos[2]};
            C = new double[]{vertices[faces[j*3+2]*3] - pos[0], vertices[faces[j*3+2]*3+1] - pos[1], vertices[faces[j*3+2]*3+2] - pos[2]};
            double Ax = A[kx] - Sx * A[kz];
            double Ay = A[ky] - Sy * A[kz];
            double Bx = B[kx] - Sx * B[kz];
            double By = B[ky] - Sy * B[kz];
            double Cx = C[kx] - Sx * C[kz];
            double Cy = C[ky] - Sy * C[kz];

            double U = Cx*By - Cy*Bx;
            double V = Ax*Cy - Ay*Cx;
            double W = Bx*Ay - By*Ax;

            if(U < 0 || V < 0 || W < 0) continue;

            double det = U + V + W;
            if(det == 0D) continue;

            double Az = Sz*A[kz];
            double Bz = Sz*B[kz];
            double Cz = Sz*C[kz];
            double T = U*Az + V*Bz + W*Cz;

            if(T <= 0 || T >= t * det) continue;

            boolean detSign = det > 0;
            boolean TSign = T > 0;
            boolean sign = Boolean.logicalXor(detSign, TSign);
//            if(!sign) continue; //TODO fix condition

            double rcpDet = 1D/det;
            u = U*rcpDet;
            v = V*rcpDet;
            w = W*rcpDet;
            t = T*rcpDet;
        }

        if(t != Double.MAX_VALUE) col = new double[]{u, v, w};
        else
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

    private void boundColor(double[] color)
    {
        color[0] = Math.min(color[0], 1D);
        color[1] = Math.min(color[1], 1D);
        color[2] = Math.min(color[2], 1D);
        color[0] = Math.max(color[0], 0D);
        color[1] = Math.max(color[1], 0D);
        color[2] = Math.max(color[2], 0D);
    }

    private class RenderKernel extends Kernel
    {
        @Local double[] cameraPos;
        @Local double[] rays;
        @Local int[] kIdxs;
        @Local double[] shearFactors;
        @Local double[] vertices;
        @Local int[] faces;

        public synchronized void update(double[] cameraPos, double[] rays, int[] kIdxs, double[] shearFactors, double[] vertices, int[] faces) {
            this.cameraPos = cameraPos;
            this.rays = rays;
            this.kIdxs = kIdxs;
            this.shearFactors = shearFactors;
            this.vertices = vertices;
            this.faces = faces;
        }

        @Override
        public void run()
        {
            int i = getGlobalId();

            double[] ray = Arrays.copyOfRange(rays, i*3, i*3+3);
            double[] col;

            double[] A, B, C;
            int kx = kIdxs[i*3], ky = kIdxs[i*3+1], kz = kIdxs[i*3+2];
            double Sx = shearFactors[i*3], Sy = shearFactors[i*3+1], Sz = shearFactors[i*3+2];

            double t = Double.MAX_VALUE, u = Double.MAX_VALUE, v = Double.MAX_VALUE, w = Double.MAX_VALUE;

            for (int j = 0; j < faces.length/3; j++) {
                A = new double[]{vertices[faces[j*3]*3] - cameraPos[0], vertices[faces[j*3]*3+1] - cameraPos[1], vertices[faces[j*3]*3+2] - cameraPos[2]};
                B = new double[]{vertices[faces[j*3+1]*3] - cameraPos[0], vertices[faces[j*3+1]*3+1] - cameraPos[1], vertices[faces[j*3+1]*3+2] - cameraPos[2]};
                C = new double[]{vertices[faces[j*3+2]*3] - cameraPos[0], vertices[faces[j*3+2]*3+1] - cameraPos[1], vertices[faces[j*3+2]*3+2] - cameraPos[2]};
                double Ax = A[kx] - Sx * A[kz];
                double Ay = A[ky] - Sy * A[kz];
                double Bx = B[kx] - Sx * B[kz];
                double By = B[ky] - Sy * B[kz];
                double Cx = C[kx] - Sx * C[kz];
                double Cy = C[ky] - Sy * C[kz];

                double U = Cx*By - Cy*Bx;
                double V = Ax*Cy - Ay*Cx;
                double W = Bx*Ay - By*Ax;

                if(U < 0 || V < 0 || W < 0) continue;

                double det = U + V + W;
                if(det == 0D) continue;

                double Az = Sz*A[kz];
                double Bz = Sz*B[kz];
                double Cz = Sz*C[kz];
                double T = U*Az + V*Bz + W*Cz;

                if(T <= 0 || T >= t * det) continue;

                boolean detSign = det > 0;
                boolean TSign = T > 0;
                boolean sign = Boolean.logicalXor(detSign, TSign);
//            if(!sign) continue; //TODO fix condition

                double rcpDet = 1D/det;
                u = U*rcpDet;
                v = V*rcpDet;
                w = W*rcpDet;
                t = T*rcpDet;
            }

            if(t != Double.MAX_VALUE) col = new double[]{u, v, w};
            else
            {
                // Background
                double rayDirLength = Math.sqrt(ray[0]*ray[0] + ray[1]*ray[1] + ray[2]*ray[2]);
                double[] unitDir = new double[]{ray[0]/rayDirLength, ray[1]/rayDirLength, ray[2]/rayDirLength};
                double a = 0.5 * (unitDir[2] + 1D);

                col = new double[]{(1D - a) * 1 + a * 0.5, (1D - a) * 1 + a * 0.7, (1D - a) * 1 + a};
            }

            boundColor(col);
            int[] color = new int[]{(int) (col[0] * 255), (int) (col[1] * 255), (int) (col[2] * 255)};
            pixels[i] = 0xFF000000 + (color[0]<<16) + (color[1]<<8) + color[2];
        }
    }
}
