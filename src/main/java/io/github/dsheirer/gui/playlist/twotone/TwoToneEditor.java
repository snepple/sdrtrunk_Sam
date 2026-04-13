package io.github.dsheirer.gui.playlist.twotone;

import io.github.dsheirer.playlist.PlaylistManager;
import io.github.dsheirer.playlist.TwoToneConfiguration;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.nio.file.Path;






/**
 * Placeholder UI for Two Tone configurations mapping and Discovery Log.
 * Uses standard MigLayout and JavaFX combinations as defined in application memory.
 */
public class TwoToneEditor extends VBox
{
    private final PlaylistManager mPlaylistManager;
    private TableView<TwoToneConfiguration> mTableView;
    private TextField mAliasField;
    private TextField mToneAField;
    private TextField mToneBField;
    private TextField mTemplateField;
    private TextField mZelloChannelField;
    private CheckBox mZelloAlertEnabledBox;
    private ComboBox<String> mAlertToneFileBox;

    public TwoToneEditor(PlaylistManager playlistManager)
    {
        mPlaylistManager = playlistManager;
        setSpacing(10);
        setPadding(new Insets(10));

        mTableView = new TableView<>();

        TableColumn<TwoToneConfiguration, String> aliasCol = new TableColumn<>("Alias");
        aliasCol.setCellValueFactory(new PropertyValueFactory<>("alias"));

        TableColumn<TwoToneConfiguration, String> templateCol = new TableColumn<>("Template");
        templateCol.setCellValueFactory(new PropertyValueFactory<>("template"));

        TableColumn<TwoToneConfiguration, String> channelCol = new TableColumn<>("Zello Channel");
        channelCol.setCellValueFactory(new PropertyValueFactory<>("zelloChannel"));

        mTableView.getColumns().addAll(aliasCol, templateCol, channelCol);

        // Refresh items
        ObservableList<TwoToneConfiguration> configs = FXCollections.observableArrayList(mPlaylistManager.getCurrentPlaylist().getTwoToneConfigurations());
        mTableView.setItems(configs);

        getChildren().add(mTableView);
        VBox.setVgrow(mTableView, Priority.ALWAYS);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        mAliasField = new TextField();
        form.addRow(0, new Label("Alias:"), mAliasField);

        mToneAField = new TextField();
        form.addRow(1, new Label("Tone A (Hz):"), mToneAField);

        mToneBField = new TextField();
        form.addRow(2, new Label("Tone B (Hz):"), mToneBField);

        mTemplateField = new TextField();
        form.addRow(3, new Label("Template:"), mTemplateField);

        mZelloChannelField = new TextField();
        form.addRow(4, new Label("Zello Channel:"), mZelloChannelField);

        mZelloAlertEnabledBox = new CheckBox("Enable Zello Alert Tone");
        form.addRow(5, mZelloAlertEnabledBox);

        mAlertToneFileBox = new ComboBox<>();
        populateAlertFiles();

        mAlertToneFileBox.disableProperty().bind(mZelloAlertEnabledBox.selectedProperty().not());

        Button previewBtn = new Button("Preview");
        previewBtn.setOnAction(e -> previewAudio());

        HBox fileBox = new HBox(10, mAlertToneFileBox, previewBtn);
        form.addRow(6, new Label("Alert Tone File:"), fileBox);

        getChildren().add(form);

        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("New");
        Button delBtn = new Button("Delete");
        Button cloneBtn = new Button("Clone");

        buttonBox.getChildren().addAll(addBtn, delBtn, cloneBtn);
        getChildren().add(buttonBox);

        setupBindings();
        setupActions(addBtn, delBtn, cloneBtn);
    }

    private void populateAlertFiles() {
        try {
            Path dir = Paths.get("src/main/resources/alert_sounds");
            if (Files.exists(dir) && Files.isDirectory(dir)) {
                List<String> files = Files.list(dir)
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.endsWith(".wav") || name.endsWith(".mp3"))
                    .collect(Collectors.toList());
                mAlertToneFileBox.setItems(FXCollections.observableArrayList(files));
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    private void previewAudio() {
        String filename = mAlertToneFileBox.getValue();
        if(filename != null && !filename.isEmpty()) {
            try {
                File f = new File("src/main/resources/alert_sounds/" + filename);
                if(f.exists()) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(AudioSystem.getAudioInputStream(f));
                    clip.start();
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private void setupBindings() {
        mTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                mAliasField.setText(newSel.getAlias());
                mToneAField.setText(String.valueOf(newSel.getToneA()));
                mToneBField.setText(String.valueOf(newSel.getToneB()));
                mTemplateField.setText(newSel.getTemplate());
                mZelloChannelField.setText(newSel.getZelloChannel());
                mZelloAlertEnabledBox.setSelected(newSel.isZelloAlertToneEnabled());
                mAlertToneFileBox.setValue(newSel.getAlertToneFile());
            } else {
                mAliasField.clear();
                mToneAField.clear();
                mToneBField.clear();
                mTemplateField.clear();
                mZelloChannelField.clear();
                mZelloAlertEnabledBox.setSelected(false);
                mAlertToneFileBox.setValue(null);
            }
        });

        // Add listeners to update the current selection
        mAliasField.textProperty().addListener((obs, o, n) -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) { sel.setAlias(n); mTableView.refresh(); }
        });
        mTemplateField.textProperty().addListener((obs, o, n) -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) { sel.setTemplate(n); mTableView.refresh(); }
        });
        mZelloChannelField.textProperty().addListener((obs, o, n) -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) { sel.setZelloChannel(n); mTableView.refresh(); }
        });
        mZelloAlertEnabledBox.selectedProperty().addListener((obs, o, n) -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) { sel.setZelloAlertToneEnabled(n); }
        });
        mAlertToneFileBox.valueProperty().addListener((obs, o, n) -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) { sel.setAlertToneFile(n); }
        });

        mToneAField.textProperty().addListener((obs, o, n) -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) {
                try { sel.setToneA(Double.parseDouble(n)); } catch (Exception e) {}
            }
        });
        mToneBField.textProperty().addListener((obs, o, n) -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) {
                try { sel.setToneB(Double.parseDouble(n)); } catch (Exception e) {}
            }
        });
    }

    private void setupActions(Button add, Button delete, Button clone) {
        add.setOnAction(e -> {
            TwoToneConfiguration config = new TwoToneConfiguration();
            config.setAlias("New Two Tone");
            mPlaylistManager.getCurrentPlaylist().getTwoToneConfigurations().add(config);
            mTableView.getItems().add(config);
            mTableView.getSelectionModel().select(config);
        });

        delete.setOnAction(e -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) {
                mPlaylistManager.getCurrentPlaylist().getTwoToneConfigurations().remove(sel);
                mTableView.getItems().remove(sel);
            }
        });

        clone.setOnAction(e -> {
            TwoToneConfiguration sel = mTableView.getSelectionModel().getSelectedItem();
            if(sel != null) {
                TwoToneConfiguration config = sel.copyOf();
                config.setAlias(sel.getAlias() + " Copy");
                mPlaylistManager.getCurrentPlaylist().getTwoToneConfigurations().add(config);
                mTableView.getItems().add(config);
                mTableView.getSelectionModel().select(config);
            }
        });
    }

    public void process(TwoToneTabRequest request)
    {
    }
}
