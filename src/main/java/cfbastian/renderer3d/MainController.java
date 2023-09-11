package cfbastian.renderer3d;

import cfbastian.renderer3d.math.Vector3;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;

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

    static int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    long startTime;

    Robot robot = new Robot();

    @FXML
    public void initialize()
    {
        Arrays.fill(pixels, 0xFF000000);

        image = new WritableImage(Application.WIDTH, Application.HEIGHT);
        pixelWriter = image.getPixelWriter();
        pixelFormat = WritablePixelFormat.getIntArgbInstance();
        pixelWriter.setPixels(0, 0, Application.WIDTH, Application.HEIGHT, pixelFormat, pixels, 0, Application.WIDTH);
        imageView.setImage(image);

        cameraController = new CameraController(new Camera(new Vector3(0D, 0D, 0D), 0D, Math.PI/2D, 90D), 0.002, 0.02, 0.5, 1.0);

        renderer.initScene();

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

            renderer.updateScene(elapsedTime);

            pixels = renderer.render(elapsedTime, cameraController.getCameraRays(), cameraController.getCameraPos());

            pixelWriter.setPixels(0, 0, Application.WIDTH, Application.HEIGHT, pixelFormat, pixels, 0, Application.WIDTH);
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

    private static void createMainScene()
    {

    }

    public static int getPixelsLength() {
        return pixels.length;
    }

    double mouseX = 0D, mouseY = 0D;
    boolean first = true; // TODO fix this hack

    @FXML
    public void onMouseMoved(MouseEvent mouseEvent)
    {
        if(mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED && !first) cameraController.ChangeCameraAngle(mouseEvent.getSceneX() - mouseX, mouseEvent.getSceneY() - mouseY);
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