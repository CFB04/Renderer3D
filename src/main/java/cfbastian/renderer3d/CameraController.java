package cfbastian.renderer3d;

import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;
import javafx.scene.input.KeyCode;

public class CameraController {

    private final double sensitivityLowerBound, sensitivityUpperBound;
    private double sensitivitySetting, mouseSensitivity, movementSpeed;
    private double lastTime;

    int[] keys;

    private final Camera camera;

    public CameraController(Camera camera, double sensitivityLowerBound, double sensitivityUpperBound, double sensitivitySetting, double movementSpeed) {
        this.camera = camera;
        this.sensitivityLowerBound = sensitivityLowerBound;
        this.sensitivityUpperBound = sensitivityUpperBound;
        this.sensitivitySetting = sensitivitySetting;
        this.mouseSensitivity = ScalarMath.weightedAvg(sensitivityLowerBound, sensitivityUpperBound, sensitivitySetting);
        this.movementSpeed = movementSpeed;

        keys = new int[6]; // [W, A, S, D, SPACE, SHIFT]
    }

    public void ChangeCameraAngle(double mouseDX, double mouseDY)
    {
        camera.setAngle(camera.getTheta() - mouseDX * mouseSensitivity, camera.getPhi() + mouseDY * mouseSensitivity);
    }

    public void moveCamera(double dX, double dY, double dZ)
    {
        camera.setPos(VectorMath.add(camera.getPos(), new Vector3(dX, dY, dZ)));
    }

    public void updatePosition(double elapsedTime)
    {
        Vector3 direction = VectorMath.normalize(VectorMath.multiply(camera.getDir(), new Vector3(1D, 1D,0D)));
        Vector3 directionPerp = new Vector3(-direction.y, direction.x, 0D);
        Vector3 vertical = new Vector3(0D, 0D, 1D);

        double dt = elapsedTime - lastTime;
        lastTime = elapsedTime;

        camera.setPos(VectorMath.add(camera.getPos(), VectorMath.scale(VectorMath.add(
                VectorMath.scale(direction, keys[0]),
                VectorMath.scale(direction, -keys[2]),
                VectorMath.scale(directionPerp, keys[1]),
                VectorMath.scale(directionPerp, -keys[3]),
                VectorMath.scale(vertical, keys[4]),
                VectorMath.scale(vertical, -keys[5])), dt * movementSpeed)));
    }

    public double[] getCameraPos()
    {
        return camera.getPos().toArray();
    }

    public double[] getCameraRays() {
        return camera.getRays();
    }

    public int[] getCameraKIdxs() {
        return camera.getkIdxs();
    }

    public double[] getCameraShearFactors() {
        return camera.getShearFactors();
    }

    public void setKey(KeyCode keyCode, boolean value) {
        int v = value? 1 : 0;
        switch (keyCode) {
            case W -> keys[0] = v;
            case A -> keys[1] = v;
            case S -> keys[2] = v;
            case D -> keys[3] = v;
            case SPACE -> keys[4] = v;
            case SHIFT -> keys[5] = v;
        }
    }
}
