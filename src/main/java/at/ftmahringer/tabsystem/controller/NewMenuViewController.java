package at.ftmahringer.tabsystem.controller;

import at.ftmahringer.tabsystem.model.Tab;
import at.ftmahringer.tabsystem.repositories.TabRespository;
import at.ftmahringer.tabsystem.utils.SceneManager;
import at.ftmahringer.tabsystem.utils.Scenes;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class NewMenuViewController implements Initializable {

    public SplitPane rootPane;
    public SplitPane rightPane;
    public ScrollPane mainPane;
    public ScrollPane tabsPane;
    public VBox bottomVbox;
    public HBox breadCrumbHbox;

    private SceneManager sceneManager;
    private final TabRespository tabRepository = TabRespository.getInstance();


    private HBox selectedTab = null;
    private final Map<HBox, Rectangle> tabIndicators = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sceneManager = SceneManager.getInstance();
        sceneManager.setScrollPane(mainPane);
        sceneManager.setBreadcrumbHBox(breadCrumbHbox);
        configureSplitPanes();
        configureTabPaneFocus();
        loadTabs();
        System.out.println("NewMenuViewController initialized");
    }

    private void loadTabs() {
        VBox tabsContainer = createTabsContainer();
        List<Tab> allTabs = tabRepository.findAllActive();

        loadPrioritizedTabs(tabsContainer, allTabs);
        loadBottomTabs(allTabs);

        tabsPane.setContent(tabsContainer);
    }

    private void loadBottomTabs(List<Tab> allTabs) {
        List<Tab> bottomTabs = allTabs.stream()
                .filter(tab -> tab.getPriorityOrder() == 0)
                .collect(Collectors.toList());

        bottomTabs.forEach(tab -> {
            HBox tabBox = createTabBox(tab);
            bottomVbox.getChildren().add(tabBox);
        });
    }

    private void loadPrioritizedTabs(VBox tabsContainer, List<Tab> allTabs) {
        List<Tab> prioritizedTabs = allTabs.stream()
                .filter(tab -> tab.getPriorityOrder() > 0)
                .sorted(Comparator.comparingInt(Tab::getPriorityOrder))
                .collect(Collectors.toList());

        prioritizedTabs.forEach(tab -> {
            HBox tabBox = createTabBox(tab);
            tabsContainer.getChildren().add(tabBox);

            if (isInitialTab(tab)) {
                handleTabClick(tabBox, tab, tabIndicators.get(tabBox));
            }
        });
    }

    private boolean isInitialTab(Tab tab) {
        return tab.getName().equalsIgnoreCase(Scenes.getFirstScene().getTitle());
    }

    private VBox createTabsContainer() {
        VBox tabsContainer = new VBox();
        tabsContainer.setSpacing(5);
        return tabsContainer;
    }

    private void configureSplitPanes() {
        setFixedDividerPosition(rootPane, 0.2);
        setFixedDividerPosition(rightPane, 0.125);
    }

    private void setFixedDividerPosition(SplitPane pane, double position) {
        pane.getDividers().getFirst().positionProperty().addListener((observable, oldValue, newValue) -> {
            pane.setDividerPositions(position);
        });
    }

    private void configureTabPaneFocus() {
        tabsPane.focusedProperty().addListener((_, _, isNowFocused) -> {
            if (isNowFocused) {
                mainPane.requestFocus();
            }
        });
    }

    private HBox createTabBox(Tab tab) {
        FontIcon tabIcon = createTabIcon(tab);
        Label tabLabel = new Label(tab.getName());

        HBox tabBox = new HBox();
        tabBox.setAlignment(Pos.CENTER_LEFT);
        tabBox.getStyleClass().add("tab");
        tabBox.setSpacing(10);

        Rectangle indicator = createTabIndicator();
        tabIndicators.put(tabBox, indicator);

        tabBox.getChildren().addAll(indicator, tabIcon, tabLabel);
        tabBox.setOnMouseClicked(event -> handleTabClick(tabBox, tab, indicator));

        return tabBox;
    }

    private FontIcon createTabIcon(Tab tab) {
        FontIcon tabIcon = new FontIcon(Optional.ofNullable(tab.getIcon()).orElse("enty-traffic-cone"));
        tabIcon.setIconSize(24);
        tabIcon.setIconColor(Color.BLACK);
        return tabIcon;
    }

    private Rectangle createTabIndicator() {
        Rectangle indicator = new Rectangle(5, 30);
        indicator.setFill(Color.BLACK);
        return indicator;
    }

    private void handleTabClick(HBox tabBox, Tab tab, Rectangle indicator) {
        resetSelectedTabStyle();
        highlightTab(tabBox, indicator);
        switchToTabScene(tab);
    }

    private void resetSelectedTabStyle() {
        if (selectedTab != null) {
            tabIndicators.get(selectedTab).setFill(Color.BLACK);
            selectedTab.getChildren().stream()
                    .filter(node -> node instanceof Label)
                    .map(node -> (Label) node)
                    .forEach(label -> label.setTextFill(Color.BLACK));
        }
    }

    private void highlightTab(HBox tabBox, Rectangle indicator) {
        indicator.setFill(Color.GREEN);
        selectedTab = tabBox;
        selectedTab.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .forEach(label -> label.setTextFill(Color.GREEN));
    }

    private void switchToTabScene(Tab tab) {
        Scenes newScene = Scenes.fromString(tab.getName());
        sceneManager.showSceneInScrollPane(newScene);
    }

    public void onShutdownClicked(MouseEvent event) {
        sceneManager.close();
    }
}
