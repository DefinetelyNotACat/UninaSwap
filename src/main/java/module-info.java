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
    requires java.dotenv;
    requires org.postgresql.jdbc;
    requires java.compiler;
    requires java.net.http;
    requires annotations;
    requires javafx.swing;
    requires java.desktop;
    requires org.jfree.jfreechart;

    opens com.example.uninaswap to javafx.fxml;
    opens com.example.uninaswap.boundary to javafx.fxml;
    opens com.example.uninaswap.controller to javafx.fxml;

    exports com.example.uninaswap;
    exports com.example.uninaswap.boundary;
    exports com.example.uninaswap.controller;
}