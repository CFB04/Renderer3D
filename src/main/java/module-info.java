module cfbastian.renderer3d {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens cfbastian.renderer3d to javafx.fxml;
    exports cfbastian.renderer3d;
}