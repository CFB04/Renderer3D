package cfbastian.renderer3d;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.*;

import java.util.Arrays;

public class MainController {

    @FXML
    ImageView imageView;

    WritableImage image;
    PixelWriter pixelWriter;
    WritablePixelFormat<java.nio.IntBuffer> pixelFormat;

    RenderLoop renderLoop = new RenderLoop();
    Renderer renderer = new Renderer();

    int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    long startTime;

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

        @Override
        public void handle(long now) {

            double elapsedTime = (now - startTime)/1000000000D;

            renderer.updateScene(elapsedTime);

            pixels = renderer.render(elapsedTime);

            pixelWriter.setPixels(0, 0, Application.WIDTH, Application.HEIGHT, pixelFormat, pixels, 0, Application.WIDTH);
            imageView.setImage(image);
        }
    }

    private static void createMainScene()
    {

    }
}