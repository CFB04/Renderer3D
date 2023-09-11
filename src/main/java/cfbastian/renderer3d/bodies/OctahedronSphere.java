package cfbastian.renderer3d.bodies;

public class OctahedronSphere extends Mesh{

    // See sebastian lagues procedural moons and planets

    public OctahedronSphere(double[] pos, String key) {
        super(pos, key);

        calculateAbsoluteVertices();
    }
}
