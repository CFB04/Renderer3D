package cfbastian.renderer3d.bodies;

public abstract class Mesh {
    protected double[] pos, vertices, absoluteVertices; // Anchor position, points relative to anchor, absolute position of points
    protected int[] tris; // (sets of indices)
    private final String key;

    // TODO add vertex normals array

    public Mesh(double[] pos, String key) { // TODO add constructor that reads points, tris from file
        this.pos = pos;
        this.key = key;
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

    public double[] getAbsoluteVertices() {
        return absoluteVertices;
    }

    public int[] getTris() {
        return tris;
    }

    public int getNumTris()
    {
        return tris.length / 3;
    }
}
