package cfbastian.renderer3d;

import cfbastian.renderer3d.math.Vector3;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;

public class MainController {

    @FXML
    ImageView imageView;

    WritableImage image;
    PixelWriter pixelWriter;
    WritablePixelFormat<java.nio.IntBuffer> pixelFormat;

    CameraController cameraController;
    RenderLoop renderLoop = new RenderLoop();
    Renderer renderer = new Renderer();

    static int[] pixels = new int[Application.width * Application.height];

    long startTime;

    @FXML
    public void initialize()
    {
        Arrays.fill(pixels, 0xFF000000);

        imageView.setFitWidth(Application.width);
        imageView.setFitHeight(Application.height);
        image = new WritableImage(Application.width, Application.height);
        pixelWriter = image.getPixelWriter();
        pixelFormat = WritablePixelFormat.getIntArgbInstance();
        pixelWriter.setPixels(0, 0, Application.width, Application.height, pixelFormat, pixels, 0, Application.width);
        imageView.setImage(image);

        cameraController = new CameraController(new Camera(new Vector3(0f, 0f, 0f), 0f, (float) (Math.PI/2f), 90f), 0.002f, 0.02f, 0.5f, 1.0f);

        renderer.init();

        startTime = System.nanoTime();
        renderLoop.start();
    }


    private class RenderLoop extends AnimationTimer {

        double timer;
        int frames;

        @Override
        public void handle(long now) {

            double elapsedTime = (now - startTime)/1000000000D;

            cameraController.updatePosition(elapsedTime);

            pixels = renderer.render(elapsedTime, cameraController.getCameraRays(), cameraController.getCameraKIdxs(), cameraController.getCameraShearFactors(), cameraController.getCameraPos());

            pixelWriter.setPixels(0, 0, Application.width, Application.height, pixelFormat, pixels, 0, Application.width);
            imageView.setImage(image);

            frames++;

            while(elapsedTime - timer > 1)
            {
                System.out.println(frames);
                timer++;
                frames = 0;
            }
        }
    }

    public static int getPixelsLength() {
        return pixels.length;
    }

    double mouseX = 0D, mouseY = 0D;
    boolean first = true; // TODO fix this hack

    @FXML
    public void onMouseMoved(MouseEvent mouseEvent)
    {
        if(mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED && !first) cameraController.ChangeCameraAngle((float) (mouseEvent.getSceneX() - mouseX), (float) (mouseEvent.getSceneY() - mouseY));
        mouseX = mouseEvent.getSceneX();
        mouseY = mouseEvent.getSceneY();
        first = false;
    }

    @FXML
    public void onKeyPress(KeyEvent keyEvent)
    {
        cameraController.setKey(keyEvent.getCode(), true);
    }

    @FXML
    public void onKeyRelease(KeyEvent keyEvent)
    {
        cameraController.setKey(keyEvent.getCode(), false);
    }
}