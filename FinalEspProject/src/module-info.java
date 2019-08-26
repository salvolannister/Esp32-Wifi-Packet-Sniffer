module FinalEspProject {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.swing;
    requires javafx.media;
    requires java.sql;
    requires sqlite.jdbc;
    requires transitive jfxtras.fxml;
    requires transitive jfxtras.common;
    requires transitive jfxtras.controls;
    requires commons.math3;


    opens application;
}