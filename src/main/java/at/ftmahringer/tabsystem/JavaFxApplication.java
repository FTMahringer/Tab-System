package at.ftmahringer.tabsystem;

import at.ftmahringer.tabsystem.utils.SceneManager;
import at.ftmahringer.tabsystem.utils.Scenes;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class JavaFxApplication extends Application {
    private static ConfigurableApplicationContext springContext;

    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }

    public static SceneManager getSceneManager() {
        return springContext.getBean(SceneManager.class);
    }

    @Override
    public void init() {
        // Initialize Spring context
        springContext = new SpringApplicationBuilder(SpringApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) {
        // Get SceneManager from Spring context
        SceneManager sceneManager = springContext.getBean(SceneManager.class);

        // Set the primary stage in SceneManager
        sceneManager.setPrimaryStage(primaryStage);

        // Show the initial scene (replace `Scenes.START` with your actual starting scene enum)
        sceneManager.showScene(Scenes.MAIN_MENU);
    }

    @Override
    public void stop() {
        // Close Spring context on application shutdown
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}