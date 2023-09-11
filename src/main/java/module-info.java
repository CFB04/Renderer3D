module cfbastian.renderer3d {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires aparapi;
    requires obj;

    opens cfbastian.renderer3d to javafx.fxml;
    exports cfbastian.renderer3d;
}