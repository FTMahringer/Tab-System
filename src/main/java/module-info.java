module at.ftmahringer.tabsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.sql;

    opens at.ftmahringer.tabsystem to javafx.fxml;
    exports at.ftmahringer.tabsystem;

    opens at.ftmahringer.tabsystem.utils to javafx.fxml;
    exports at.ftmahringer.tabsystem.utils;

    opens at.ftmahringer.tabsystem.controller to javafx.fxml;
    exports at.ftmahringer.tabsystem.controller;

    opens at.ftmahringer.tabsystem.controller.tabs to javafx.fxml;
    exports at.ftmahringer.tabsystem.controller.tabs;

    opens at.ftmahringer.tabsystem.model to javafx.fxml;
    exports at.ftmahringer.tabsystem.model;

    opens at.ftmahringer.tabsystem.repositories to javafx.fxml;
    exports at.ftmahringer.tabsystem.repositories;
}