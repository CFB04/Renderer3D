package cfbastian.renderer3d.util;

import cfbastian.renderer3d.bodies.Mesh;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;

import java.io.*;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;

public final class ObjFileManager {

    private static BufferedReader br;

    public static Mesh generateMeshFromFile(File file, double[] pos, int uvDimensions, String key) throws IOException
    {
        InputStream objInputStream = new FileInputStream(file);
        Obj obj = ObjReader.read(objInputStream);

        float[] vertexArray = ObjData.getVerticesArray(obj);
        float[] texCoordArray = ObjData.getTexCoordsArray(obj, uvDimensions);
        float[] normalArray = ObjData.getNormalsArray(obj);

        double[] vertices = IntStream.range(0, vertexArray.length).mapToDouble(i -> vertexArray[i]).toArray();
        double[] texCoords = IntStream.range(0, texCoordArray.length).mapToDouble(i -> texCoordArray[i]).toArray();
        double[] normals = IntStream.range(0, normalArray.length).mapToDouble(i -> normalArray[i]).toArray();
        int[] faceIndices = ObjData.getFaceVertexIndicesArray(obj, 3);
        int[] faceTextureCoords = ObjData.getFaceTexCoordIndicesArray(obj);
        int[] faceNormals = ObjData.getFaceNormalIndicesArray(obj);

        return new Mesh(pos, vertices, texCoords, normals, faceIndices, faceTextureCoords, faceNormals, uvDimensions, key);
    }

    public static Mesh generateMeshFromFile(String filepath, double[] pos, int uvDimensions, String key) throws IOException
    {
        return generateMeshFromFile(new File(filepath), pos, uvDimensions, key);
    }
//
//    public static Mesh generateMeshFromFile(File file, double[] pos, String key) throws IOException // TODO assert that filetype is .obj
//    {
//        br = new BufferedReader(new FileReader(file));
//
//        ArrayList<Double> vsList = new ArrayList<>();
//        ArrayList<Double> vtsList = new ArrayList<>();
//        ArrayList<Double> vnsList = new ArrayList<>();
//        ArrayList<Integer> fsList = new ArrayList<>();
//
//        boolean areVTs3D = false, doFsIncludeVTs = false, doFsIncludeVNs = false;
//
//        while(br.ready())
//        {
//            String line = br.readLine();
//            Scanner s = new Scanner(line);
//
//            if(line.contains("v "))
//            {
//                while(s.hasNextDouble()) vsList.add(s.nextDouble());
//            }
//            else if (line.contains("vt "))
//            {
//                ArrayList<Double> values = new ArrayList<>();
//                while(s.hasNextDouble()) values.add(s.nextDouble());
//
//                if(values.size() == 3) areVTs3D = true;
//
//                vtsList.addAll(values);
//            }
//            else if (line.contains("vn "))
//            {
//                while(s.hasNextDouble()) vnsList.add(s.nextDouble());
//            }
//            else if (line.contains("f "))
//            {
//                ArrayList<Integer> values = new ArrayList<>();
//                while(s.hasNextInt()) values.add(s.nextInt());
//
//                if(values.size() == 3) fsList.addAll(values);
//                else if(values.size() == 6)
//                {
//                    if(line.replaceAll(" ", "").contains("//")) doFsIncludeVNs = true;
//                    else doFsIncludeVTs = true;
//                } else if (values.size() == 9) {
//                    doFsIncludeVNs = true;
//                    doFsIncludeVTs = true;
//                }
//
//                fsList.addAll(values);
//            }
//        }
//
//        br.close();
//
//        double[] vs = vsList.stream().mapToDouble(Double::doubleValue).toArray();
//        double[] vns = vnsList.stream().mapToDouble(Double::doubleValue).toArray();
//        double[] vts = vtsList.stream().mapToDouble(Double::doubleValue).toArray();
//        int[] fsTemp = fsList.stream().mapToInt(Integer::intValue).toArray();
//
//        int di = 1;
//        di += doFsIncludeVTs? 1 : 0;
//        di += doFsIncludeVNs? 1 : 0;
//
//        int[] fs = new int[fsTemp.length/di], uvs = new int[fsTemp.length/di], normals = new int[fsTemp.length/di];
//
//        for (int i = 0; i < fs.length; i++) fs[i] = fsTemp[i*di];
//
//
//        if(di == 2)
//        {
//            if(doFsIncludeVTs) for (int i = 0; i < fs.length; i++) uvs[i] = fsTemp[1 + i*di];
//            else for (int i = 0; i < fs.length; i++) normals[i] = fsTemp[1 + i*di];
//        }
//        else if (di == 3)
//        {
//            for (int i = 0; i < fs.length; i++)
//            {
//                uvs[i] = fsTemp[1 + i*di];
//                normals[i] = fsTemp[2 + i*di];
//            }
//        }
//        return new Mesh(pos, vs, vts, vns, fs, uvs, normals, areVTs3D, key);
//    }
}
