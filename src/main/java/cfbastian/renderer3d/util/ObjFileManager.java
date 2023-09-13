package cfbastian.renderer3d.util;

import cfbastian.renderer3d.bodies.Mesh;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;

import java.io.*;

public final class ObjFileManager {

    public static Mesh generateMeshFromFile(File file, float[] pos, float scale, int uvDimensions, String key) throws IOException
    {
        InputStream objInputStream = new FileInputStream(file);
        Obj obj = ObjReader.read(objInputStream);

        float[] vertices = ObjData.getVerticesArray(obj);
        float[] texCoords = ObjData.getTexCoordsArray(obj, uvDimensions);
        float[] normals = ObjData.getNormalsArray(obj);

        int[] faceIndices = ObjData.getFaceVertexIndicesArray(obj, 3);
        int[] faceTextureCoords = ObjData.getFaceTexCoordIndicesArray(obj);
        int[] faceNormals = ObjData.getFaceNormalIndicesArray(obj);

        return new Mesh(pos, scale, vertices, texCoords, normals, faceIndices, faceTextureCoords, faceNormals, uvDimensions, key);
    }

    public static Mesh generateMeshFromFile(String filepath, float[] pos, float scale, int uvDimensions, String key) throws IOException
    {
        return generateMeshFromFile(new File(filepath), pos, scale, uvDimensions, key);
    }
}
