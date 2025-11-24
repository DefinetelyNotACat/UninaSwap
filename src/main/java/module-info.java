module com.example.uninaswap {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires javafx.graphics;
    requires spring.security.crypto;
    requires commons.logging;
    requires com.example.uninaswap;
    opens com.example.uninaswap to javafx.fxml;
    opens boundary to javafx.fxml;
    exports com.example.uninaswap;
    exports boundary;
}