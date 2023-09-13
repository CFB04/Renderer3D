package cfbastian.renderer3d.util;

import cfbastian.renderer3d.bodies.Mesh;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;

import java.io.*;
import java.util.stream.IntStream;

public final class ObjFileManager {

    private static BufferedReader br;

    public static Mesh generateMeshFromFile(File file, float[] pos, float scale, int uvDimensions, String key) throws IOException
    {
        InputStream objInputStream = new FileInputStream(file);
        Obj obj = ObjReader.read(objInputStream);

        float[] vertexArray = ObjData.getVerticesArray(obj);
        float[] texCoordArray = ObjData.getTexCoordsArray(obj, uvDimensions);
        float[] normalArray = ObjData.getNormalsArray(obj);

//        double[] vertices = IntStream.range(0, vertexArray.length).mapToDouble(i -> vertexArray[i]).toArray();
//        double[] texCoords = IntStream.range(0, texCoordArray.length).mapToDouble(i -> texCoordArray[i]).toArray();
//        double[] normals = IntStream.range(0, normalArray.length).mapToDouble(i -> normalArray[i]).toArray();
        int[] faceIndices = ObjData.getFaceVertexIndicesArray(obj, 3);
        int[] faceTextureCoords = ObjData.getFaceTexCoordIndicesArray(obj);
        int[] faceNormals = ObjData.getFaceNormalIndicesArray(obj);

        //return new Mesh(pos, scale, vertices, texCoords, normals, faceIndices, faceTextureCoords, faceNormals, uvDimensions, key);
        return new Mesh(pos, scale, vertexArray, texCoordArray, normalArray, faceIndices, faceTextureCoords, faceNormals, uvDimensions, key);
    }

    public static Mesh generateMeshFromFile(String filepath, float[] pos, float scale, int uvDimensions, String key) throws IOException
    {
        return generateMeshFromFile(new File(filepath), pos, scale, uvDimensions, key);
    }
}
