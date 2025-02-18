package at.ftmahringer.tabsystem.utils;

public enum Scenes {
    MAIN_MENU("controller/main-menu-view.fxml", "Menu", false, false),
    DASHBOARD("controller/tabs/dashboard-view.fxml", "Dashboard", false, true),
    ;

    final String fxmlPath;
    final String title;
    final boolean isPopup;
    final boolean isTab;

    Scenes(String fxmlPath, String title, boolean isPopup, boolean isTab) {
        this.fxmlPath = fxmlPath;
        this.title = title;
        this.isPopup = isPopup;
        this.isTab = isTab;
    }

    public static Scenes getFirstScene() {
        return DASHBOARD;
    }

    public String getPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPopup() {
        return isPopup;
    }

    public boolean isTab() {
        return isTab;
    }

    public static Scenes fromString(String text) {
        for (Scenes b : Scenes.values()) {
            if (b.title.equalsIgnoreCase(text)) {
                return b;
            }
        }
        System.out.println("No scene found for title: " + text);
        return null;
    }

}