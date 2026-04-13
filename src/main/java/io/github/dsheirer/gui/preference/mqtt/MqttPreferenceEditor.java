package io.github.dsheirer.gui.preference.mqtt;

import io.github.dsheirer.preference.UserPreferences;
import io.github.dsheirer.preference.mqtt.MqttPreference;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class MqttPreferenceEditor extends VBox
{
    private UserPreferences mUserPreferences;

    public MqttPreferenceEditor(UserPreferences userPreferences)
    {
        mUserPreferences = userPreferences;
        MqttPreference preference = userPreferences.getMqttPreference();

        setPadding(new Insets(10));
        setSpacing(10);

        Label header = new Label("MQTT Settings");
        header.setStyle("-fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        CheckBox enableCheckBox = new CheckBox("Enable MQTT");
        enableCheckBox.setSelected(preference.isEnabled());

        TextField serverField = new TextField(preference.getServer());
        TextField usernameField = new TextField(preference.getUsername());
        PasswordField passwordField = new PasswordField();
        passwordField.setText(preference.getPassword());

        serverField.disableProperty().bind(enableCheckBox.selectedProperty().not());
        usernameField.disableProperty().bind(enableCheckBox.selectedProperty().not());
        passwordField.disableProperty().bind(enableCheckBox.selectedProperty().not());

        grid.add(enableCheckBox, 0, 0, 2, 1);
        grid.add(new Label("Server/Host:"), 0, 1);
        grid.add(serverField, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(usernameField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);

        enableCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> preference.setEnabled(newValue));
        serverField.textProperty().addListener((observable, oldValue, newValue) -> preference.setServer(newValue));
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> preference.setUsername(newValue));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> preference.setPassword(newValue));

        getChildren().addAll(header, grid);
    }
}
