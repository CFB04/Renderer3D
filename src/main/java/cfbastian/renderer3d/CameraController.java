package cfbastian.renderer3d;

import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;
import javafx.scene.input.KeyCode;

public class CameraController {

    private final float sensitivityLowerBound, sensitivityUpperBound;
    private float sensitivitySetting, mouseSensitivity, movementSpeed;
    private double lastTime;

    int[] keys;

    private final Camera camera;

    public CameraController(Camera camera, float sensitivityLowerBound, float sensitivityUpperBound, float sensitivitySetting, float movementSpeed) {
        this.camera = camera;
        this.sensitivityLowerBound = sensitivityLowerBound;
        this.sensitivityUpperBound = sensitivityUpperBound;
        this.sensitivitySetting = sensitivitySetting;
        this.sensitivitySetting = ScalarMath.bound(this.sensitivitySetting, this.sensitivityLowerBound, this.sensitivityUpperBound);
        this.mouseSensitivity = ScalarMath.weightedAvg(sensitivityLowerBound, sensitivityUpperBound, sensitivitySetting);
        this.movementSpeed = movementSpeed;


        keys = new int[6]; // [W, A, S, D, SPACE, SHIFT]
    }

    public void ChangeCameraAngle(float mouseDX, float mouseDY)
    {
        camera.setAngle(camera.getTheta() - mouseDX * mouseSensitivity, camera.getPhi() + mouseDY * mouseSensitivity);
    }

    public void moveCamera(float dX, float dY, float dZ)
    {
        camera.setPos(VectorMath.add(camera.getPos(), new Vector3(dX, dY, dZ)));
    }

    public void updatePosition(double elapsedTime)
    {
        Vector3 direction = VectorMath.normalize(VectorMath.multiply(camera.getDir(), new Vector3(1f, 1f,0f)));
        Vector3 directionPerp = new Vector3(-direction.y, direction.x, 0f);
        Vector3 vertical = new Vector3(0f, 0f, 1f);

        double dt = elapsedTime - lastTime;
        lastTime = elapsedTime;

        camera.setPos(VectorMath.add(camera.getPos(), VectorMath.scale(VectorMath.add(
                VectorMath.scale(direction, keys[0]),
                VectorMath.scale(direction, -keys[2]),
                VectorMath.scale(directionPerp, keys[1]),
                VectorMath.scale(directionPerp, -keys[3]),
                VectorMath.scale(vertical, keys[4]),
                VectorMath.scale(vertical, -keys[5])), (float) (dt * movementSpeed))));
    }

    public float[] getCameraPos()
    {
        return camera.getPos().toArray();
    }

    public float[] getCameraRays() {
        return camera.getRays();
    }

    public int[] getCameraKIdxs() {
        return camera.getKIdxs();
    }

    public float[] getCameraShearFactors() {
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

    public void setMouseSensitivity(float sensitivitySetting)
    {
        this.sensitivitySetting = sensitivitySetting;
        this.sensitivitySetting = ScalarMath.bound(this.sensitivitySetting, this.sensitivityLowerBound, this.sensitivityUpperBound);
        this.mouseSensitivity = ScalarMath.weightedAvg(sensitivityLowerBound, sensitivityUpperBound, sensitivitySetting);
    }

    public void setMovementSpeed(float movementSpeed)
    {
        this.movementSpeed = movementSpeed;
    }
}
