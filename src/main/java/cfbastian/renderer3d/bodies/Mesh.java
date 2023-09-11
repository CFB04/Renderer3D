package cfbastian.renderer3d.bodies;

public abstract class Mesh {
    protected double[] pos, points, absolutePoints; // Anchor position, points relative to anchor, absolute position of points
    protected int[] tris; // (sets of indices)
    private final String key;

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
        calculateAbsolutePoints();
    }

    protected void calculateAbsolutePoints()
    {
        for (int i = 0; i < points.length; i++) absolutePoints[i] = points[i] + pos[i % 3];
    }

    public double[] getAbsolutePoints() {
        return absolutePoints;
    }

    public int[] getTris() {
        return tris;
    }

    public int getNumTris()
    {
        return tris.length / 3;
    }
}
