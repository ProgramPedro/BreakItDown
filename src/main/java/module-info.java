module com.example.breakitdown {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.google.gson;
    requires org.jfree.jfreechart;
    requires javafx.swing;

    opens com.example.breakitdown to javafx.fxml;
    exports com.example.breakitdown;
}