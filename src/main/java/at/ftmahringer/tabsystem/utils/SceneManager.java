package at.ftmahringer.tabsystem.utils;

import at.ftmahringer.tabsystem.JavaFxApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class SceneManager {

    private final ApplicationContext springContext;
    private Stage primaryStage;
    private Stage popUpStage;
    private final Deque<Scenes> sceneStack = new ArrayDeque<>();
    private final Deque<Scenes> popUpSceneStack = new ArrayDeque<>();
    private final Deque<Scenes> scrollPaneSceneStack = new ArrayDeque<>();
    private final Map<Scenes, Object> activeControllers = new HashMap<>();

    private ScrollPane scrollPane;
    private HBox breadcrumbHBox;

    public SceneManager(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    // Loads the scene and assigns its controller via Spring Context
    private Parent loadScene(Scenes fxml) {
        return loadScene(fxml, null);
    }

    private Parent loadScene(Scenes fxml, Class<?> customControllerClass) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(JavaFxApplication.class.getResource(fxml.getPath()));

            if (customControllerClass != null) {
                Object customController = springContext.getBean(customControllerClass);
                fxmlLoader.setController(customController);
                activeControllers.put(fxml, customController);
            } else {
                fxmlLoader.setControllerFactory(springContext::getBean);
            }

            Parent root = fxmlLoader.load();
            if (!activeControllers.containsKey(fxml)) {
                activeControllers.put(fxml, fxmlLoader.getController());
            }
            return root;
        } catch (IOException e) {
            throw new RuntimeException("Error loading scene: " + fxml, e);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Stage getPopUpStage() {
        return popUpStage;
    }

    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void setBreadcrumbHBox(HBox breadcrumbHBox) {
        this.breadcrumbHBox = breadcrumbHBox;
    }

    public void showScene(Scenes fxml) {
        validateSceneType(fxml, false, false);
        Parent root = loadScene(fxml);
        showSceneOnStage(fxml, primaryStage, sceneStack, root);
    }

    public void showSceneInScrollPane(Scenes fxml) {
        validateSceneType(fxml, true, false);
        Parent root = loadScene(fxml);
        scrollPane.setContent(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        updateBreadcrumbs();
    }

    public void showSceneInScrollPane(Scenes fxml, Scenes rootScene) {
        validateSceneType(fxml, true, false);
        if (!scrollPaneSceneStack.contains(rootScene)) {
            scrollPaneSceneStack.addLast(rootScene);
        }
        scrollPaneSceneStack.push(fxml);
        showSceneInScrollPane(fxml);
    }

    public void goBackInScrollPane() {
        if (scrollPaneSceneStack.size() > 1) {
            scrollPaneSceneStack.pop();
            showSceneInScrollPane(scrollPaneSceneStack.peek());
        }
    }

    public void showPopUp(Scenes fxml, Window parentWindow) {
        showPopUpHelper(fxml, parentWindow);
    }

    private void showPopUpHelper(Scenes fxml, Window parentWindow) {
        validateSceneType(fxml, false, true);
        if (popUpStage == null) {
            initializePopUpStage(parentWindow);
        }
        Parent root = loadScene(fxml);
        showSceneOnStage(fxml, popUpStage, popUpSceneStack, root);
    }

    public void goBack() {
        if (sceneStack.size() > 1) {
            sceneStack.pop();
            Scenes previousScene = sceneStack.peek();
            Parent root = loadScene(previousScene);
            showSceneOnStage(previousScene, primaryStage, sceneStack, root);
        }
    }

    public void goBackPopUp() {
        if (popUpSceneStack.size() > 1) {
            popUpSceneStack.pop();
            Scenes previousScene = popUpSceneStack.peek();
            Parent root = loadScene(previousScene);
            showSceneOnStage(previousScene, popUpStage, popUpSceneStack, root);
        } else {
            closePopUpWindow();
        }
    }

    public void closePopUpWindow() {
        if (popUpStage != null) {
            popUpStage.close();
            popUpStage = null;
        }
        popUpSceneStack.clear();
    }

    public void resetBreadcrumbs(Scenes rootScene) {
        scrollPaneSceneStack.clear();
        scrollPaneSceneStack.push(rootScene);
    }

    private void updateBreadcrumbs() {
        breadcrumbHBox.getChildren().clear();
        List<Scenes> breadcrumbs = new ArrayList<>(scrollPaneSceneStack);
        Collections.reverse(breadcrumbs);
        for (Scenes breadcrumb : breadcrumbs) {
            addBreadcrumb(breadcrumb, breadcrumbs.indexOf(breadcrumb));
        }
    }

    private void navigateToBreadcrumb(int index) {
        if (index < 0 || index >= scrollPaneSceneStack.size()) return;
        while (scrollPaneSceneStack.size() > index + 1) {
            scrollPaneSceneStack.pop();
        }
        showSceneInScrollPane(scrollPaneSceneStack.peek());
    }

    public void addBreadcrumb(Scenes childScene, int index) {
        Button button = new Button(childScene.getTitle());
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #007BFF; -fx-font-weight: bold; -fx-cursor: hand;");
        button.setOnAction(_ -> navigateToBreadcrumb(index));
        if (index > 0) {
            breadcrumbHBox.getChildren().add(new Label(" / "));
        }
        breadcrumbHBox.getChildren().add(button);
    }

    public Object getCurrentController() {
        if (!scrollPaneSceneStack.isEmpty()) {
            return activeControllers.get(scrollPaneSceneStack.peek());
        }
        return activeControllers.get(sceneStack.peek());
    }

    public Scenes getCurrentScene() {
        return sceneStack.peek();
    }

    public void close() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }

    private void validateSceneType(Scenes fxml, boolean isTab, boolean isPopup) {
        if (fxml == null) {
            throw new IllegalArgumentException("Scene cannot be null.");
        }
        if (isPopup && !fxml.isPopup()) {
            throw new IllegalArgumentException("Non-popup scene passed to popup method.");
        } else if (isTab && !fxml.isTab()) {
            throw new IllegalArgumentException("Non-tab scene passed to tab method.");
        } else if (!isTab && !isPopup && (fxml.isPopup() || fxml.isTab())) {
            throw new IllegalArgumentException("Invalid scene type for this method.");
        }
    }

    private void initializePopUpStage(Window parentWindow) {
        popUpStage = new Stage();
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.initOwner(parentWindow);
    }

    private void showSceneOnStage(Scenes fxml, Stage stage, Deque<Scenes> stack, Parent root) {
        Scene scene = new Scene(root);
        stack.push(fxml);
        stage.setTitle(fxml.getTitle());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}
