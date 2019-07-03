/**
 * Copyright 2018 Jignesh Dhua.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jigneshdhua.tools.jsonpad.controller.ui;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jigneshdhua.tools.jsonpad.controller.factory.ControllerFactory;
import com.jigneshdhua.tools.jsonpad.controller.factory.StageController;
import com.jigneshdhua.tools.jsonpad.ui.view.EditorTab;
import com.jigneshdhua.tools.jsonpad.utils.Utils;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public final class MainWindowController extends StageController {

    private final static String TAG = MainWindowController.class.getSimpleName();

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private Menu menuNew;

    @FXML
    private MenuItem itemLoad;

    @FXML
    private MenuItem itemSave;

    @FXML
    private MenuItem itemSaveAs;

    @FXML
    private MenuItem itemClose;

    @FXML
    private MenuItem itemAbout;

    @FXML
    private TabPane tabPane;

    @FXML
    private Label statusLabel;

    // private final ArrayList<EditorTabController> tabControllers;
    private FileChooser fileChooser;

    private int tabCounter;

    public static void create(Application application, Stage stage) throws Exception {
        FXMLLoader.load(MainWindowController.class.getResource("/fxml/MainWindow.fxml"),
                ResourceBundle.getBundle("fxml.ui"), null, new ControllerFactory(application, stage));
    }

    public MainWindowController(Application application, Stage stage) {
        super(application, stage);
        // tabControllers = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        MenuItem menuItem = new MenuItem("JSON");
        menuItem.setOnAction((ActionEvent event) -> createNewEditorTab(
                new File(getResources().getString("new_file") + " " + tabCounter++ /* tabControllers.size() */),
                false));

        menuNew.getItems().add(menuItem);

        itemLoad.setOnAction(event -> {
            loadFile(null);
            event.consume();
        });

        itemSave.setOnAction(event -> {
            EditorTab editorTab = (EditorTab) tabPane.getSelectionModel().getSelectedItem();
            saveContent(editorTab.getEditorTabController());
            event.consume();

        });

        itemSaveAs.setOnAction(event -> {
            EditorTab editorTab = (EditorTab) tabPane.getSelectionModel().getSelectedItem();
            saveAsContent(editorTab.getEditorTabController());
            event.consume();
        });

        itemClose.setOnAction(event -> {
            onStageClose();
            event.consume();
        });
        itemAbout.setOnAction(event -> {
            event.consume();
        });

        tabPane.setOnDragOver(event -> {
            if (event.getGestureSource() != tabPane && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        tabPane.setOnDragEntered(event -> {
            if (event.getGestureSource() != tabPane && event.getDragboard().hasFiles()) {
                tabPane.setOpacity(0.7f);
                tabPane.setBackground(
                        new Background(new BackgroundFill(Color.LIGHTGREY, CornerRadii.EMPTY, Insets.EMPTY)));
            }
            event.consume();
        });
        tabPane.setOnDragExited(event -> {
            if (event.getGestureSource() != tabPane && event.getDragboard().hasFiles()) {
                tabPane.setOpacity(1f);
                tabPane.setBackground(Background.EMPTY);
            }
            event.consume();
        });
        tabPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                db.getFiles().forEach((file) -> {
                    createNewEditorTab(file, true);
                });
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        fileChooser = new FileChooser();
        fileChooser.setInitialFileName("");
        // mFileChooser.getExtensionFilters().addAll(ParserFileType.getExtensionFilters());

        Stage stage = getStage();
        stage.setTitle(resources.getString("appname"));
        stage.setScene(new Scene(rootBorderPane));
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.setOnCloseRequest(event -> {
            onStageClose();
            event.consume();
        });
        stage.show();

        List<String> params = getApplication().getParameters().getRaw();
        for (String file : params) {
            File diskFile = new File(file);
            if (diskFile.exists()) {
                loadFile(diskFile);
            }
        }
    }

    private void loadFile(File file) {
        ResourceBundle resourceBundle = getResources();

        if (file == null) {
            fileChooser.setTitle(resourceBundle.getString("load_title"));
            file = fileChooser.showOpenDialog(getStage());
            if (file != null) {
                fileChooser.setInitialDirectory(file.getParentFile());
            }
        }

        if (file != null) {
            // We load file according to their extensions, not the content
            createNewEditorTab(file, true);
        }
    }

    private void createNewEditorTab(File file, boolean loadFile) {

        try {
            EditorTabController newTabController = EditorTabController.create(getApplication(), getStage());

            EditorTab newTab = new EditorTab(file, newTabController);
            newTab.setClosable(true);
            newTab.setContent(newTabController.getRoot());
            newTab.setOnCloseRequest(event -> {
                // todo: show yes/no save dialog
//                if (newTabController.isEdited()) {
//                    newTabController.saveContent();
//                }
                saveContent(newTabController);
                // tabControllers.remove(newTabController);
                if (tabPane.getTabs().size() == 1) {

                    statusLabel.setText("");
                }
            });
            newTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    // EditorTabController tabController =
                    // tabControllers.get(tabPane.getTabs().indexOf(newTab));
                    EditorTabController tabController = newTab.getEditorTabController();
                    tabController.onEditorTabSelected();

                }
            });

            newTabController.setEditorPane(newTab);
            newTabController.setStatusLabel(statusLabel);
            // tabControllers.add(newTabController);
            tabPane.getTabs().add(newTab);
            if (loadFile) {
                newTabController.loadContent();
                newTabController.setNew(false);
            }
            // mParserChooser.setDisable(false);
            tabPane.getSelectionModel().select(newTab);
        } catch (Exception ex) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
            if (ex.getCause() != null) {
                Utils.showErrorDialog(null, ex.getCause().getLocalizedMessage());
            } else {
                Utils.showErrorDialog(null, ex.getLocalizedMessage());
            }
        }
    }

    private void saveContent(EditorTabController tabController) {

        if (tabController.isEdited()) {

            File file = tabController.getEditorTab().getFile();

            if (tabController.isNew()) {
                file = fileChooser.showSaveDialog(getStage());
                tabController.getEditorTab().setFile(file);
            }
            tabController.saveContent();
        }
    }

    private void saveAsContent(EditorTabController tabController) {

        File file = fileChooser.showSaveDialog(getStage());

        tabController.getEditorTab().setFile(file);
        tabController.saveContent();
    }

    private void onStageClose() {
        // check if tabs are edited and build a list with them
//        tabControllers.forEach(tabController -> {
//            saveContent(tabController);
//        });

        tabPane.getTabs().forEach(tab -> {
            saveContent(((EditorTab) tab).getEditorTabController());
        });
        getStage().hide();
    }
}