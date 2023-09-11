package cfbastian.renderer3d;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.robot.Robot;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    ImageView imageView;

    WritableImage image;
    PixelWriter pixelWriter;
    WritablePixelFormat<java.nio.IntBuffer> pixelFormat;

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

            renderer.updateScene(elapsedTime);

            pixels = renderer.render(elapsedTime);

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
    public void look(MouseEvent mouseEvent)
    {
        if(mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED && !first) renderer.ChangeCameraAngle(mouseEvent.getSceneX() - mouseX, mouseEvent.getSceneY() - mouseY);
        mouseX = mouseEvent.getSceneX();
        mouseY = mouseEvent.getSceneY();
        first = false;
    }

    @FXML
    public void keyPress(KeyEvent keyEvent)
    {

    }

    @FXML
    public void keyRelease(KeyEvent keyEvent)
    {

    }
}