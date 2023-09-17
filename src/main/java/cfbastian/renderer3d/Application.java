package cfbastian.renderer3d;

import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    public static int width = 1280, height = 720;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("renderer-view.fxml"));
        scene = new Scene(fxmlLoader.load(), width, height);

        scene.setFill(Color.LIGHTGRAY);
        scene.getRoot().requestFocus();

        stage.setResizable(false);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void setCursor(Cursor cursor)
    {
        scene.setCursor(cursor);
    }

    public static double getX()
    {
        return scene.getX();
    }

    public static double getY()
    {
        return scene.getY();
    }
}