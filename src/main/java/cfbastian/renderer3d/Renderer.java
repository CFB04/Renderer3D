package cfbastian.renderer3d;

import cfbastian.renderer3d.bodies.AxisAlignedBoundingBox;
import cfbastian.renderer3d.bodies.BoundingVolumeHierarchy;
import cfbastian.renderer3d.util.ObjFileManager;
import com.aparapi.Kernel;
import com.aparapi.Range;

import java.io.IOException;
import java.util.Arrays;

public class Renderer {
    private Scene mainScene;
    private BoundingVolumeHierarchy bvh;
    private BoundingVolumeHierarchy[] bvhLeaves;
    private AxisAlignedBoundingBox aabb;
    double elapsedTime;

    float[] rays, vertices;
    int[] faces;
    int[] kIdxs;
    float[] shearFactors;
    float[] cameraPos;

    RenderKernel renderKernel = new RenderKernel();
    Range range;

    public void init()
    {
        range = Range.create(Application.width * Application.height, 32);

        mainScene = new Scene();
        try {
//            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/StanfordBunny.obj", new float[]{6f, 0f, -2f}, 5f, 2, "StaffordBunny"));
//            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/UtahTeapot.obj", new float[]{6f, 0f, 0f}, 5f, 2, "Teapot"));
            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/Sphere.obj", new float[]{4f, 0f, 0f}, 0.5f, 2, "Sphere"));
//            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/Cube.obj", new float[]{4f, 0f, 0f}, 0.5f, 2, "Cube"));
//            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/Cube.obj", new float[]{4f, 1.5f, 0f}, 0.5f, 2, "Cube1"));
//            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/Cube.obj", new float[]{4f, -1.5f, 0f}, 0.5f, 2, "Cube2"));
//            mainScene.addMesh(ObjFileManager.generateMeshFromFile("src/main/resources/cfbastian/renderer3d/meshes/Quad.obj", new float[]{5f, 0f, 0f}, 1f, 2, "Quad"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        bvh = new BoundingVolumeHierarchy(mainScene.getAllFaces(), mainScene.getAllVertices(), 1, 99, 3, 0.01f);
        bvhLeaves = bvh.getLeaves();
//        System.out.println(Arrays.toString(bvh.getLeaves()));
//        System.out.println(Arrays.toString(bvh.getFaces()));
//        System.out.println(bvh.getFaces().length);
        for (BoundingVolumeHierarchy leaf : bvh.getLeaves())
        {
            System.out.println(Arrays.toString(leaf.getFaces()));
            System.out.println(leaf.getFaces().length);
        }

        bvh.mapTree();

        System.out.println("BVH");
        System.out.println(Arrays.toString(bvh.getMap()));
        System.out.println(Arrays.toString(bvh.getBoundingBoxes()));

        vertices = mainScene.getAllVertices();
        faces = mainScene.getAllFaces();

        aabb = new AxisAlignedBoundingBox(faces, vertices, 0.01f);
        System.out.println(Arrays.toString(aabb.getAB()));
    }

    public int[] render(double elapsedTime, float[] rays, int[] kIdxs, float[] shearFactors, float[] cameraPos)
    {
        this.elapsedTime = elapsedTime;
        this.rays = rays;
        this.kIdxs = kIdxs;
        this.shearFactors = shearFactors;
        this.cameraPos = cameraPos;

        renderKernel.update(cameraPos, rays, kIdxs, shearFactors, vertices, faces, aabb.getAB());
        renderKernel.execute(range);

        return renderKernel.get();
    }

    private class RenderKernel extends Kernel
    {
        @Local float[] cameraPos;
        @Local float[] rays;
        @Local int[] kIdxs;
        @Local float[] shearFactors;
        @Local float[] vertices;
        @Local int[] faces;
        @Local int[] pixels = new int[Application.width * Application.height];

        @Local float[] aabb;

        @PrivateMemorySpace(3) float[] ray;

        public synchronized void update(float[] cameraPos, float[] rays, int[] kIdxs, float[] shearFactors, float[] vertices, int[] faces, float[] aabb) {
            this.cameraPos = cameraPos;
            this.rays = rays;
            this.kIdxs = kIdxs;
            this.shearFactors = shearFactors;
            this.vertices = vertices;
            this.faces = faces;

            this.aabb = aabb;

            this.ray = new float[3];
        }

        public int[] get()
        {
            return pixels;
        }

        public boolean hitAABB(float[] cameraPos, float[] ray)
        {
            float tMin = Float.MIN_VALUE, tMax = Float.MAX_VALUE;

            for (int i = 0; i < 3; i++) {
                float invD = 1f/ray[i];
                float t0 = (aabb[i] - cameraPos[i]) * invD;
                float t1 = (aabb[i+3] - cameraPos[i]) * invD;

                if(invD < 0f)
                {
                    float v = t0;
                    t0 = t1;
                    t1 = v;
                }

                if(t0 > tMin) tMin = t0;
                if(t1 < tMax) tMax = t1;

                if(tMax < tMin) return false;
            }

            return true;
        }

        /**
         * Gets the color for the pixel at the given index. Ray-Triangle intersection algorithm from
         * Watertight Ray/Triangle Intersection by Woop et al. TODO fix citation
         * */
        @Override
        public void run()
        {
            int i = getGlobalId();

            ray[0] = rays[i*3];
            ray[1] = rays[i*3+1];
            ray[2] = rays[i*3+2];
//            float rayX = rays[i*3];
//            float rayY = rays[i*3+1];
//            float rayZ = rays[i*3+2];

            float r, g, b;
            float t = 3.4028235E38f, u = 0f, v = 0f, w = 0f; // 3.4028235E38f is Float.MAX_VALUE


            if(hitAABB(cameraPos, ray)) {
                int kx = kIdxs[i * 3], ky = kIdxs[i * 3 + 1], kz = kIdxs[i * 3 + 2];
                float Sx = shearFactors[i * 3], Sy = shearFactors[i * 3 + 1], Sz = shearFactors[i * 3 + 2];

                for (int j = 0; j < faces.length / 3; j++) {
                    float Akx = vertices[faces[j * 3] * 3 + kx] - cameraPos[kx];
                    float Aky = vertices[faces[j * 3] * 3 + ky] - cameraPos[ky];
                    float Akz = vertices[faces[j * 3] * 3 + kz] - cameraPos[kz];
                    float Bkx = vertices[faces[j * 3 + 1] * 3 + kx] - cameraPos[kx];
                    float Bky = vertices[faces[j * 3 + 1] * 3 + ky] - cameraPos[ky];
                    float Bkz = vertices[faces[j * 3 + 1] * 3 + kz] - cameraPos[kz];
                    float Ckx = vertices[faces[j * 3 + 2] * 3 + kx] - cameraPos[kx];
                    float Cky = vertices[faces[j * 3 + 2] * 3 + ky] - cameraPos[ky];
                    float Ckz = vertices[faces[j * 3 + 2] * 3 + kz] - cameraPos[kz];

                    float Ax = Akx - Sx * Akz;
                    float Ay = Aky - Sy * Akz;
                    float Bx = Bkx - Sx * Bkz;
                    float By = Bky - Sy * Bkz;
                    float Cx = Ckx - Sx * Ckz;
                    float Cy = Cky - Sy * Ckz;

                    float U = Cx * By - Cy * Bx;
                    float V = Ax * Cy - Ay * Cx;
                    float W = Bx * Ay - By * Ax;

                    if (U < 0 || V < 0 || W < 0) continue;

                    float det = U + V + W;
                    if (det == 0D) continue;

                    float Az = Sz * Akz;
                    float Bz = Sz * Bkz;
                    float Cz = Sz * Ckz;
                    float T = U * Az + V * Bz + W * Cz;

                    if (T <= 0 || T >= t * det) continue;

                    float rcpDet = 1f / det;
                    u = U * rcpDet;
                    v = V * rcpDet;
                    w = W * rcpDet;
                    t = T * rcpDet;
                }
            }

            if(t != 3.4028235E38f) // Float.MAX_VALUE
            {
                r = u;
                g = v;
                b = w;
            }
            else
            {
                // Background
                float rayDirLength = sqrt(ray[0]*ray[0] + ray[1]*ray[1] + ray[2]*ray[2]);
                float[] unitDir = new float[]{ray[0]/rayDirLength, ray[1]/rayDirLength, ray[2]/rayDirLength};
                float a = 0.5f * (unitDir[2] + 1f);

                r = (1f - a) * 1 + a * 0.5f;
                g = (1f - a) * 1 + a * 0.7f;
                b = (1f - a) * 1 + a;

//                if(aabb.hitAABB(cameraPos, new float[]{rayX, rayY, rayZ}, 0.01f))
//                {
//                    r *= 0.9f;
//                    g *= 0.9f;
//                    b *= 0.9f;
//                }
            }
            if(hitAABB(cameraPos, ray))
            {
                r *= 0.9f;
                b *= 0.9f;
                g *= 0.9f;
            }

//            for (BoundingVolumeHierarchy leaf : bvhLeaves) {
//                if(leaf.getAabb().hitAABB(cameraPos, ray)) {
//                    r *= 0.9f;
//                    g *= 0.9f;
//                    b *= 0.9f;
//                }
//            }

            r = min(r, 1f);
            g = min(g, 1f);
            b = min(b, 1f);
            r = max(r, 0f);
            g = max(g, 0f);
            b = max(b, 0f);
            pixels[i] = 0xFF000000 + ((int) (r * 255)<<16) + ((int) (g * 255)<<8) + (int) (b * 255);
        }
    }
}
