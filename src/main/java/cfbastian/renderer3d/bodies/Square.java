package cfbastian.renderer3d.bodies;

public class Square extends Mesh{

    public Square(double[] pos, String key) {
        super(pos, key);

        this.points = new double[]{
                0.0, 0.0, 0.0,
                0.0, 0.0, 1.0,
                1.0, 0.0, 0.0,
                1.0, 0.0, 1.0};

        this.tris = new int[]{0, 1, 2, 2, 1, 3};

        calculateAbsolutePoints();
    }
}
