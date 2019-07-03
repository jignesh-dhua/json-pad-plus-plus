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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import com.jigneshdhua.tools.jsonpad.controller.factory.ControllerFactory;
import com.jigneshdhua.tools.jsonpad.controller.factory.StageController;
import com.jigneshdhua.tools.jsonpad.ui.view.EditorTab;
import com.jigneshdhua.tools.jsonpad.utils.Utils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public final class EditorTabController extends StageController {

    private final static String TAG = EditorTabController.class.getSimpleName();

    @FXML
    private BorderPane rootBorderPane;

    private CodeArea codeArea;

    private SplitPane splitPane;

    private ContextMenu editorContextMenu;

    private String statusMessage;
    private Paint statusColor;
    private Label statusLabel;

    private EditorTab editorTab;

    private boolean isEdited;
    
    private boolean isNew = true;
    
    static EditorTabController create(Application application, Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(EditorTab.class.getResource("/fxml/EditorTab.fxml"),
                ResourceBundle.getBundle("fxml.ui"), null, new ControllerFactory(application, stage));

        loader.load();
        return loader.getController();
    }

    public EditorTabController(Application application, Stage stage) {
        super(application, stage);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        codeArea = new CodeArea();
        splitPane = new SplitPane();
        splitPane.setDividerPositions(0.2D);
        StackPane stackPane = new StackPane(new VirtualizedScrollPane<>(codeArea));

        splitPane.getItems().add(stackPane);

        rootBorderPane.setCenter(splitPane);

        editorContextMenu = createContextMenu();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setOnContextMenuRequested(event -> {
            codeArea.getContextMenu().show(getStage());
            event.consume();
        });

        codeArea.setOnKeyPressed(event ->{
            isEdited = true;
        });
        
        SplitPane.setResizableWithParent(codeArea, true);

    }

    void onEditorTabSelected() {
        // todo: later we can use an other kind of control to show character count
        // or caret position, number of words..
        setStatusMessage(statusMessage, statusColor);

        // Without this there is a bug when you open another tab then switch to a
        // previous one.
        // Without this trying to format text (ctrl+space) will always format the last
        // tab.
        codeArea.setContextMenu(null);
        codeArea.setContextMenu(editorContextMenu);
        Platform.runLater(() -> codeArea.requestFocus());
    }

    void loadContent() {
        String line;
        StringBuilder stringBuilder = new StringBuilder();

        File file = getEditorTab().getFile();
        String lineSeparator = String.format(System.lineSeparator());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(lineSeparator);
            }
        } catch (IOException ex) {
            Logger.getLogger(TAG).log(Level.SEVERE, null, ex);
        }

        try {
            codeArea.replaceText(stringBuilder.toString());
        } catch (Exception e) {
            Utils.showErrorDialog(null, e.getLocalizedMessage());
        }
    }

    private ContextMenu createContextMenu() {
        ResourceBundle resources = getResources();
        ContextMenu menu = new ContextMenu();

        MenuItem itemCopy = new MenuItem(resources.getString("copy"));
        itemCopy.setAccelerator(KeyCombination.keyCombination("CTRL+C"));
        itemCopy.setOnAction(event -> {
            codeArea.copy();
            event.consume();
        });

        MenuItem itemCut = new MenuItem(resources.getString("cut"));
        itemCut.setAccelerator(KeyCombination.keyCombination("CTRL+X"));
        itemCut.setOnAction(event -> {
            codeArea.cut();
            event.consume();
        });

        MenuItem itemPaste = new MenuItem(resources.getString("paste"));
        itemPaste.setAccelerator(KeyCombination.keyCombination("CTRL+V"));
        itemPaste.setOnAction(event -> {
            codeArea.paste();
            event.consume();
        });

        MenuItem itemPrettyPrint = new MenuItem(resources.getString("format"));
        itemPrettyPrint.setAccelerator(KeyCombination.keyCombination("CTRL+SPACE"));
        itemPrettyPrint.setOnAction(event -> {
            String code = codeArea.getText();
            try {
                // TODO: PrettyPrint
            } catch (Exception e) {
                Utils.showErrorDialog(null, e.getLocalizedMessage());
            }

            codeArea.replaceText(code);
            event.consume();
        });

        menu.getItems().addAll(itemCopy, itemCut, itemPaste, new SeparatorMenuItem(), itemPrettyPrint);
        return menu;
    }

    private void setStatusMessage(String message, Paint color) {
        statusColor = color;
        statusMessage = message;
        statusLabel.setText(statusMessage);
        statusLabel.setTextFill(statusColor);
    }

    public EditorTab getEditorTab() {
        return editorTab;
    }

    BorderPane getRoot() {
        return rootBorderPane;
    }

    boolean isEdited() {
        return isEdited;
    }

//    void saveContent()  {
//        Logger.getLogger(TAG).log(Level.INFO, "Not implemented: {0}", getEditorTab().getFile());
//        File file = getEditorTab().getFile();
//        byte[] encoded;
//        try {
//            encoded = Files.readAllBytes(file.toPath());
//        
//        String content = new String(encoded, Charset.defaultCharset());
//        
//        Logger.getLogger(TAG).log(Level.INFO, "Content: {0}", content);
//       
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(!file.exists()) {
            
//        }
        
//        Logger.getLogger(TAG).log(Level.INFO, "Tab Content: {0}", codeArea.getText());
//    }

    public void saveContent() {
        try {
            Utils.saveFile(codeArea.getText(), editorTab.getFile());
            setNew(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getContent() {
        return codeArea.getText();
    }
    void setEditorPane(EditorTab editorTab) {
        this.editorTab = editorTab;
    }

    void setStatusLabel(Label label) {
        statusLabel = label;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}