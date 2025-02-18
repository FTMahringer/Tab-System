package at.ftmahringer.tabsystem.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class CustomAlert extends Alert {
    public CustomAlert(AlertType alertType, String title, String headerText, String contentText) {
        super(alertType);
        setTitle(title);
        setHeaderText(headerText);
        setContentText(contentText);
    }

    public static boolean showConfirmation(String headerText, String contentText) {
        CustomAlert alert = new CustomAlert(AlertType.CONFIRMATION, "Confirmation", headerText, contentText);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void showWarning(String headerText, String contentText) {
        CustomAlert alert = new CustomAlert(AlertType.WARNING, "Warning", headerText, contentText);
        alert.showAndWait();
    }

    public static void showInfo(String headerText, String contentText) {
        CustomAlert alert = new CustomAlert(AlertType.INFORMATION, "Information", headerText, contentText);
        alert.showAndWait();
    }

    public static void showError(String headerText, String contentText) {
        CustomAlert alert = new CustomAlert(AlertType.ERROR, "Error", headerText, contentText);
        alert.showAndWait();
    }
}
