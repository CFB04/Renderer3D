package cfbastian.renderer2d;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MainController {

    @FXML
    ImageView imageView;

    WritableImage image;
    PixelWriter pixelWriter;
    WritablePixelFormat<java.nio.IntBuffer> pixelFormat;

    RenderLoop renderLoop = new RenderLoop();

    int[] pixels = new int[Application.WIDTH * Application.HEIGHT];

    long startTime;

    @FXML
    public void initialize()
    {
        Arrays.fill(pixels, 0x00000000);

        for (int i = 0; i < Application.WIDTH * Application.HEIGHT; i++)
        {
            pixels[i] = 0xFF000000 + i;
        }

        image = new WritableImage(Application.WIDTH, Application.HEIGHT);
        pixelWriter = image.getPixelWriter();
        pixelFormat = WritablePixelFormat.getIntArgbInstance();
        pixelWriter.setPixels(0, 0, Application.WIDTH, Application.HEIGHT, pixelFormat, pixels, 0, Application.WIDTH);
        imageView.setImage(image);

        startTime = System.nanoTime();
        renderLoop.start();
    }

    private class RenderLoop extends AnimationTimer {

        @Override
        public void handle(long now) {

            double elapsedTime = (now - startTime)/1000000000D;

            for (int i = 0; i < Application.WIDTH * Application.HEIGHT; i++)
            {
                pixels[i] = 0xFF000000 + (int) (elapsedTime * 255);
            }

            pixelWriter.setPixels(0, 0, Application.WIDTH, Application.HEIGHT, pixelFormat, pixels, 0, Application.WIDTH);
            imageView.setImage(image);
        }
    }
}