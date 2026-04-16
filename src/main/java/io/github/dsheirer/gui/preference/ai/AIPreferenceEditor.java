package io.github.dsheirer.gui.preference.ai;

import io.github.dsheirer.preference.UserPreferences;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class AIPreferenceEditor extends VBox {

    private final UserPreferences mUserPreferences;

    public AIPreferenceEditor(UserPreferences userPreferences) {
        mUserPreferences = userPreferences;

        setPadding(new Insets(10));
        setSpacing(10);

        Label apiKeyLabel = new Label("Gemini API Key:");
        PasswordField apiKeyField = new PasswordField();
        apiKeyField.setText(mUserPreferences.getAIPreference().getGeminiApiKey());
        apiKeyField.textProperty().addListener((observable, oldValue, newValue) -> {
            mUserPreferences.getAIPreference().setGeminiApiKey(newValue);
        });

        getChildren().addAll(apiKeyLabel, apiKeyField);
    }
}
