package cfbastian.renderer3d;

import cfbastian.renderer3d.math.ScalarMath;
import cfbastian.renderer3d.math.Vector3;
import cfbastian.renderer3d.math.VectorMath;

public class CameraController {

    private final double sensitivityLowerBound, sensitivityUpperBound;
    private double sensitivitySetting, mouseSensitivity;

    private final Camera camera;

    public CameraController(Camera camera, double sensitivityLowerBound, double sensitivityUpperBound, double sensitivitySetting) {
        this.camera = camera;
        this.sensitivityLowerBound = sensitivityLowerBound;
        this.sensitivityUpperBound = sensitivityUpperBound;
        this.sensitivitySetting = sensitivitySetting;
        this.mouseSensitivity = ScalarMath.weightedAvg(sensitivityLowerBound, sensitivityUpperBound, sensitivitySetting);
    }

    public void ChangeCameraAngle(double mouseDX, double mouseDY)
    {
        camera.setAngle(camera.getTheta() - mouseDX * mouseSensitivity, camera.getPhi() + mouseDY * mouseSensitivity);
    }

    public void moveCamera(double dX, double dY, double dZ)
    {
        camera.setPos(VectorMath.add(camera.getPos(), new Vector3(dX, dY, dZ)));
    }

    public double[] getCameraPos()
    {
        return camera.getPos().toArray();
    }

    public double[] getCameraRays() {
        return camera.getRays();
    }
}
