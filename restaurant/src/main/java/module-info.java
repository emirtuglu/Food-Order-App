module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.example to javafx.fxml, com.google.gson;
    exports com.example;
}