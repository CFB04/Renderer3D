package cfbastian.renderer3d.bodies;

import cfbastian.renderer3d.math.VectorMath;

import java.util.Arrays;

public class Mesh {
    protected double[] pos;
    protected double scale;
    protected double[] vertices, absoluteVertices; // Anchor position, points relative to anchor, absolute position of points
    protected double[] textureCoords;
    protected double[] vertexNormals, faceNormals;
    protected int[] faces, uvs, normals;
    protected final int uvDimensions;
    private final String key;

    public Mesh(double[] pos, double scale, double[] vertices, double[] vertexTextures, double[] vertexNormals, int[] faces, int[] uvs, int[] normals, int uvDimensions, String key) {
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

        absoluteVertices = new double[vertices.length];
        faceNormals = new double[faces.length];
        calculateAbsoluteVertices();
        calculateFaceNormals();
    }

    public String getKey() {
        return key;
    }

    public double[] getPos() {
        return pos;
    }

    public void setPos(double[] pos) {
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
            double[] normal = getNormal(
                    new double[]{vertexNormals[normals[i*3]*3], vertexNormals[normals[i*3]*3+1], vertexNormals[normals[i*3]*3+2]},
                    new double[]{vertexNormals[normals[i*3+1]*3], vertexNormals[normals[i*3+1]*3+1], vertexNormals[normals[i*3+1]*3+2]},
                    new double[]{vertexNormals[normals[i*3+2]*3], vertexNormals[normals[i*3+2]*3+1], vertexNormals[normals[i*3+2]*3+2]});
            faceNormals[i*3] = normal[0];
            faceNormals[i*3+1] = normal[1];
            faceNormals[i*3+2] = normal[2];
        }

        System.out.println(Arrays.toString(faceNormals));
    }
    public double[] getNormal(double[] v1, double[] v2, double[] v3) { // This averages the vertex normals TODO blend using UVs
        return new double[]{(v1[0] + v2[0] + v3[0]) / 3D, (v1[1] + v2[1] + v3[1]) / 3D, (v1[2] + v2[2] + v3[2]) / 3D};
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double[] getVertices() {
        return vertices;
    }

    public double[] getAbsoluteVertices() {
        return absoluteVertices;
    }

    public double[] getTextureCoords() {
        return textureCoords;
    }

    public double[] getVertexNormals() {
        return vertexNormals;
    }

    public double[] getFaceNormals() {
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
