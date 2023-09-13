package cfbastian.renderer3d.bodies;

import cfbastian.renderer3d.math.VectorMath;

import java.util.Arrays;

public class Mesh {
    protected float[] pos;
    protected float scale;
    protected float[] vertices, absoluteVertices; // Anchor position, points relative to anchor, absolute position of points
    protected float[] textureCoords;
    protected float[] vertexNormals, faceNormals;
    protected int[] faces, uvs, normals;
    protected final int uvDimensions;
    private final String key;

    public Mesh(float[] pos, float scale, float[] vertices, float[] vertexTextures, float[] vertexNormals, int[] faces, int[] uvs, int[] normals, int uvDimensions, String key) {
        this.pos = pos;
        this.scale = scale;
        this.vertices = vertices;
        this.textureCoords = vertexTextures;
        this.vertexNormals = vertexNormals;
        this.faces = faces;
        this.uvs = uvs;
        this.normals = normals;
        this.uvDimensions = uvDimensions;
        this.key = key;

        absoluteVertices = new float[vertices.length];
        faceNormals = new float[faces.length];
        calculateAbsoluteVertices();
        calculateFaceNormals();
    }

    public String getKey() {
        return key;
    }

    public float[] getPos() {
        return pos;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
        calculateAbsoluteVertices();
    }

    protected void calculateAbsoluteVertices()
    {
        for (int i = 0; i < vertices.length; i++) absoluteVertices[i] = vertices[i] * scale + pos[i % 3];
    }

    public void calculateFaceNormals()
    {
        for (int i = 0; i < normals.length/3; i++)
        {
            float[] normal = getNormal(
                    new float[]{vertexNormals[normals[i*3]*3], vertexNormals[normals[i*3]*3+1], vertexNormals[normals[i*3]*3+2]},
                    new float[]{vertexNormals[normals[i*3+1]*3], vertexNormals[normals[i*3+1]*3+1], vertexNormals[normals[i*3+1]*3+2]},
                    new float[]{vertexNormals[normals[i*3+2]*3], vertexNormals[normals[i*3+2]*3+1], vertexNormals[normals[i*3+2]*3+2]});
            faceNormals[i*3] = normal[0];
            faceNormals[i*3+1] = normal[1];
            faceNormals[i*3+2] = normal[2];
        }
    }
    public float[] getNormal(float[] v1, float[] v2, float[] v3) { // This averages the vertex normals TODO blend using UVs
        return new float[]{(v1[0] + v2[0] + v3[0]) / 3f, (v1[1] + v2[1] + v3[1]) / 3f, (v1[2] + v2[2] + v3[2]) / 3f};
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getAbsoluteVertices() {
        return absoluteVertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getVertexNormals() {
        return vertexNormals;
    }

    public float[] getFaceNormals() {
        return faceNormals;
    }

    public int[] getFaces() {
        return faces;
    }

    public int[] getUvs() {
        return uvs;
    }

    public int[] getNormals() {
        return normals;
    }

    public int getUvDimensions() {
        return uvDimensions;
    }
}
