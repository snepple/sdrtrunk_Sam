import sys

with open('src/main/java/io/github/dsheirer/gui/playlist/twotone/TwoToneEditor.java', 'r') as f:
    content = f.read()

# Block 1 imports:
# We just want to merge both of them
content = content.replace("""<<<<<<< HEAD
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





=======
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
>>>>>>> origin/master""", """import javafx.scene.control.*;
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
import javafx.beans.binding.Bindings;
import javafx.util.converter.NumberStringConverter;""")

# Block 2 variables
content = content.replace("""<<<<<<< HEAD
    private TextField mAliasField;
    private TextField mToneAField;
    private TextField mToneBField;
    private TextField mTemplateField;
    private TextField mZelloChannelField;
    private CheckBox mZelloAlertEnabledBox;
    private ComboBox<String> mAlertToneFileBox;
=======
    private ObservableList<TwoToneConfiguration> mObservableConfigs;
>>>>>>> origin/master""", """    private ObservableList<TwoToneConfiguration> mObservableConfigs;
    private TextField mAliasField;
    private TextField mToneAField;
    private TextField mToneBField;
    private TextField mTemplateField;
    private TextField mZelloChannelField;
    private CheckBox mZelloAlertEnabledBox;
    private ComboBox<String> mAlertToneFileBox;""")

with open('src/main/java/io/github/dsheirer/gui/playlist/twotone/TwoToneEditor.java', 'w') as f:
    f.write(content)
