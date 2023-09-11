package cfbastian.renderer3d.bodies;

public class Mesh {
    protected double[] pos;
    protected double[] vertices, absoluteVertices; // Anchor position, points relative to anchor, absolute position of points
    protected double[] textureCoords;
    protected double[] vertexNormals;
    protected int[] faces, uvs, normals;
    protected final int uvDimensions;
    private final String key;

    public Mesh(double[] pos, double[] vertices, double[] vertexTextures, double[] vertexNormals, int[] faces, int[] uvs, int[] normals, int uvDimensions, String key) {
        this.pos = pos;
        this.vertices = vertices;
        this.textureCoords = vertexTextures;
        this.vertexNormals = vertexNormals;
        this.faces = faces;
        this.uvs = uvs;
        this.normals = normals;
        this.uvDimensions = uvDimensions;
        this.key = key;

        absoluteVertices = new double[vertices.length];
        calculateAbsoluteVertices();
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
        for (int i = 0; i < vertices.length; i++) absoluteVertices[i] = vertices[i] + pos[i % 3];
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
