module at.ftmahringer.tabsystem {
    requires javafx.fxml;
    requires spring.core;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.boot;
    requires spring.beans;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires jakarta.annotation;
    requires spring.data.jpa;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires spring.data.commons;
    requires spring.tx;
    requires org.controlsfx.controls;

    opens at.ftmahringer.tabsystem to javafx.fxml, spring.core, spring.beans, spring.context;
    exports at.ftmahringer.tabsystem;

    opens at.ftmahringer.tabsystem.controller to javafx.fxml, spring.core, spring.beans, spring.context;
    exports at.ftmahringer.tabsystem.controller;

    opens at.ftmahringer.tabsystem.controller.tabs to javafx.fxml, spring.core, spring.beans, spring.context;
    exports at.ftmahringer.tabsystem.controller.tabs;

    opens at.ftmahringer.tabsystem.model to spring.core, spring.beans, spring.context, org.hibernate.orm.core;
    exports at.ftmahringer.tabsystem.model;

    opens at.ftmahringer.tabsystem.repositories to spring.core, spring.beans, spring.context;
    exports at.ftmahringer.tabsystem.repositories;

    opens at.ftmahringer.tabsystem.utils to spring.core, spring.beans, spring.context;
    exports at.ftmahringer.tabsystem.utils;
}