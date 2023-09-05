module cfbastian.renderer2d {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens cfbastian.renderer2d to javafx.fxml;
    exports cfbastian.renderer2d;
}