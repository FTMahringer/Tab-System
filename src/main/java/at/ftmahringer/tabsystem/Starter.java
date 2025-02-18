package at.ftmahringer.tabsystem;

import at.ftmahringer.tabsystem.utils.SceneManager;
import at.ftmahringer.tabsystem.utils.Scenes;
import javafx.application.Application;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Starter extends Application {

    private static Stage starterPage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        starterPage = primaryStage;

        SceneManager.getInstance().setPrimaryStage(starterPage);
        SceneManager.getInstance().showScene(Scenes.MAIN_MENU);

    }

    public static Stage getPrimaryStage() {
        return starterPage;
    }

    public static String getRessource(String ressource) throws URISyntaxException {
        return Paths.get(Starter.class.getResource(ressource).toURI()).toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}