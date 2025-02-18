package at.ftmahringer.tabsystem.utils;

import at.ftmahringer.tabsystem.Starter;
import at.ftmahringer.tabsystem.utils.Scenes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private Stage primaryStage;
    private Stage popUpStage;
    private final Deque<Scenes> sceneStack = new ArrayDeque<>();
    private final Deque<Scenes> popUpSceneStack = new ArrayDeque<>();
    private final Deque<Scenes> scrollPaneSceneStack = new ArrayDeque<>();
    private final Map<Scenes, Object> activeControllers = new HashMap<>();

    private ScrollPane scrollPane;
    private HBox breadcrumbHBox;

    private static SceneManager instance;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }


    private Parent loadScene(Scenes fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Paths.get(Starter.getRessource(fxml.getPath())).toUri().toURL());
            Parent root = fxmlLoader.load();
            activeControllers.put(fxml, fxmlLoader.getController());
            return root;
        } catch (IOException e) {
            throw new RuntimeException("Error loading scene: " + fxml, e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
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

    public void showScene(Scenes fxml) {
        validateSceneType(fxml, false, false);
        Parent root = loadScene(fxml);
        showSceneOnStage(fxml, primaryStage, sceneStack, root);
    }

    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void showSceneInScrollPane(Scenes fxml) {
        if (fxml == null) {
            throw new IllegalArgumentException("Scene (fxml) cannot be null in showSceneInScrollPane.");
        }
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

    // Public pop-up methods all call the helper to avoid duplicating logic.
    public void showPopUp(Scenes fxml, Window parentWindow) {
        showPopUpHelper(fxml, parentWindow);
    }

    // Handles pop-up initialization, scene loading, and data receiver assignment if needed.
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

    public Object getCurrentController() {
        if (!scrollPaneSceneStack.isEmpty()) {
            return activeControllers.get(scrollPaneSceneStack.peek()); // Return ScrollPane controller if available
        }
        return activeControllers.get(sceneStack.peek()); // Fallback to normal controller
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
            throw new IllegalArgumentException("The given Scene (fxml) is null. Please ensure that a valid Scene is passed.");
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

    // This helper method creates the scene, updates the stack, configures the stage, and shows it.
    private void showSceneOnStage(Scenes fxml, Stage stage, Deque<Scenes> stack, Parent root) {
        Scene scene = new Scene(root);
        stack.push(fxml);
        stage.setTitle(fxml.getTitle());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public Object getControllerForScene(Scenes targetScene) {
        return activeControllers.get(targetScene);
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
            if (GlobalEnumVariables.DEBUG_MODE.getValue()) {
                System.out.println("Breadcrumb: " + breadcrumb + " at index: " + breadcrumbs.indexOf(breadcrumb));
            }
            addBreadcrumb(breadcrumb, breadcrumbs.indexOf(breadcrumb));
        }
    }

    private void navigateToBreadcrumb(int index) {
        GlobalEnumVariables debugMode = GlobalEnumVariables.DEBUG_MODE;
        if (debugMode.getValue()) {
            System.out.println("Navigating to breadcrumb: " + index + " with size: " + scrollPaneSceneStack.size());
        }
        if (index < 0 || index >= scrollPaneSceneStack.size()) {
            if (debugMode.getValue()) {
                System.out.println("Invalid breadcrumb index: " + index);
            }
            return;
        }
        if (index == scrollPaneSceneStack.size() - 1) {
            if (debugMode.getValue()) {
                System.out.println("Already at breadcrumb index: " + index);
            }
            return;
        }
        while (scrollPaneSceneStack.size() > index + 1) {
            scrollPaneSceneStack.pop();
        }
        showSceneInScrollPane(scrollPaneSceneStack.peek());
    }

    public void addBreadcrumb(Scenes childScene, int index) {
        Button button = new Button(childScene.getTitle());
        button.setStyle("-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-text-fill: #007BFF;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'Arial', sans-serif;" +
                "-fx-text-alignment: center;");
        button.setOnAction(_ -> navigateToBreadcrumb(index));
        if (index > 0) {
            breadcrumbHBox.getChildren().add(new Label(" / "));
        }
        breadcrumbHBox.getChildren().add(button);
    }
}
