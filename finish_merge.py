import re

with open('src/main/java/io/github/dsheirer/gui/playlist/twotone/TwoToneEditor.java', 'r') as f:
    content = f.read()

# We want to replace the whole conflict block with our merged code.
# The code in master has added `mObservableConfigs` and new `enableMqttPublish` features.
# Let's keep master's table implementation and bindings, but add our Alert Tone UI inputs.

start = content.find("<<<<<<< HEAD")
end = content.find(">>>>>>> origin/master") + len(">>>>>>> origin/master")

merged_code = """
        mObservableConfigs = FXCollections.observableArrayList(TwoToneConfiguration.extractor());
        if (playlistManager.getCurrentPlaylist() != null) {
            mObservableConfigs.addAll(playlistManager.getCurrentPlaylist().getTwoToneConfigurations());
        }

        mTableView = new TableView<>(mObservableConfigs);

        TableColumn<TwoToneConfiguration, String> aliasCol = new TableColumn<>("Alias");
        aliasCol.setCellValueFactory(new PropertyValueFactory<>("alias"));
        TableColumn<TwoToneConfiguration, Double> toneACol = new TableColumn<>("Tone A");
        toneACol.setCellValueFactory(new PropertyValueFactory<>("toneA"));
        TableColumn<TwoToneConfiguration, Double> toneBCol = new TableColumn<>("Tone B");
        toneBCol.setCellValueFactory(new PropertyValueFactory<>("toneB"));
        TableColumn<TwoToneConfiguration, Boolean> mqttCol = new TableColumn<>("MQTT Enabled");
        mqttCol.setCellValueFactory(new PropertyValueFactory<>("enableMqttPublish"));

        mTableView.getColumns().addAll(aliasCol, toneACol, toneBCol, mqttCol);
        VBox.setVgrow(mTableView, Priority.ALWAYS);

        GridPane editorGrid = new GridPane();
        editorGrid.setHgap(10);
        editorGrid.setVgap(5);

        TextField aliasField = new TextField();
        TextField toneAField = new TextField();
        TextField toneBField = new TextField();
        TextField zelloField = new TextField();

        CheckBox mqttCheck = new CheckBox("Enable MQTT Publish");
        TextField topicField = new TextField();
        TextField messageField = new TextField();
        topicField.disableProperty().bind(mqttCheck.selectedProperty().not());
        messageField.disableProperty().bind(mqttCheck.selectedProperty().not());

        mZelloAlertEnabledBox = new CheckBox("Enable Zello Alert Tone");
        mAlertToneFileBox = new ComboBox<>();
        populateAlertFiles();
        mAlertToneFileBox.disableProperty().bind(mZelloAlertEnabledBox.selectedProperty().not());
        Button previewBtn = new Button("Preview");
        previewBtn.setOnAction(e -> previewAudio());
        HBox fileBox = new HBox(10, mAlertToneFileBox, previewBtn);

        editorGrid.add(new Label("Alias:"), 0, 0);
        editorGrid.add(aliasField, 1, 0);
        editorGrid.add(new Label("Tone A:"), 0, 1);
        editorGrid.add(toneAField, 1, 1);
        editorGrid.add(new Label("Tone B:"), 0, 2);
        editorGrid.add(toneBField, 1, 2);
        editorGrid.add(new Label("Zello Channel:"), 0, 3);
        editorGrid.add(zelloField, 1, 3);

        editorGrid.add(mqttCheck, 0, 4, 2, 1);
        editorGrid.add(new Label("MQTT Topic:"), 0, 5);
        editorGrid.add(topicField, 1, 5);
        editorGrid.add(new Label("MQTT Msg:"), 0, 6);
        editorGrid.add(messageField, 1, 6);

        editorGrid.add(mZelloAlertEnabledBox, 0, 7, 2, 1);
        editorGrid.add(new Label("Alert Tone:"), 0, 8);
        editorGrid.add(fileBox, 1, 8);

        mTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (oldV != null) {
                Bindings.unbindBidirectional(aliasField.textProperty(), oldV.aliasProperty());
                Bindings.unbindBidirectional(toneAField.textProperty(), oldV.toneAProperty());
                Bindings.unbindBidirectional(toneBField.textProperty(), oldV.toneBProperty());
                Bindings.unbindBidirectional(zelloField.textProperty(), oldV.zelloChannelProperty());
                Bindings.unbindBidirectional(mqttCheck.selectedProperty(), oldV.enableMqttPublishProperty());
                Bindings.unbindBidirectional(topicField.textProperty(), oldV.mqttTopicProperty());
                Bindings.unbindBidirectional(messageField.textProperty(), oldV.mqttMessageProperty());
                Bindings.unbindBidirectional(mZelloAlertEnabledBox.selectedProperty(), oldV.zelloAlertToneEnabledProperty());
                Bindings.unbindBidirectional(mAlertToneFileBox.valueProperty(), oldV.alertToneFileProperty());
            }
            if (newV != null) {
                Bindings.bindBidirectional(aliasField.textProperty(), newV.aliasProperty());
                Bindings.bindBidirectional(toneAField.textProperty(), newV.toneAProperty(), new NumberStringConverter());
                Bindings.bindBidirectional(toneBField.textProperty(), newV.toneBProperty(), new NumberStringConverter());
                Bindings.bindBidirectional(zelloField.textProperty(), newV.zelloChannelProperty());
                Bindings.bindBidirectional(mqttCheck.selectedProperty(), newV.enableMqttPublishProperty());
                Bindings.bindBidirectional(topicField.textProperty(), newV.mqttTopicProperty());
                Bindings.bindBidirectional(messageField.textProperty(), newV.mqttMessageProperty());
                Bindings.bindBidirectional(mZelloAlertEnabledBox.selectedProperty(), newV.zelloAlertToneEnabledProperty());
                Bindings.bindBidirectional(mAlertToneFileBox.valueProperty(), newV.alertToneFileProperty());
            } else {
                aliasField.clear();
                toneAField.clear();
                toneBField.clear();
                zelloField.clear();
                mqttCheck.setSelected(false);
                topicField.clear();
                messageField.clear();
                mZelloAlertEnabledBox.setSelected(false);
                mAlertToneFileBox.setValue(null);
            }
        });

        HBox btnBox = new HBox(10);
        Button btnAdd = new Button("Add Tone");
        btnAdd.setOnAction(evt -> {
            TwoToneConfiguration c = new TwoToneConfiguration();
            c.setAlias("New");
            mObservableConfigs.add(c);
            playlistManager.getCurrentPlaylist().getTwoToneConfigurations().add(c);
        });
        Button btnDel = new Button("Delete");
        btnDel.setOnAction(evt -> {
            TwoToneConfiguration c = mTableView.getSelectionModel().getSelectedItem();
            if (c != null) {
                mObservableConfigs.remove(c);
                playlistManager.getCurrentPlaylist().getTwoToneConfigurations().remove(c);
            }
        });
        btnBox.getChildren().addAll(btnAdd, btnDel);

        getChildren().addAll(mTableView, editorGrid, btnBox);
"""

content = content[:start] + merged_code + content[end:]

with open('src/main/java/io/github/dsheirer/gui/playlist/twotone/TwoToneEditor.java', 'w') as f:
    f.write(content)
