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
package com.jigneshdhua.tools.jsonpad.ui.view;

import javafx.scene.control.*;
import javafx.util.Pair;

import java.util.ResourceBundle;

public class EditorTreeTableRow<T> extends TreeTableRow<Pair<String, T>> {

    private final ResourceBundle resourceBundle;
    private final ContextMenu contextMenu;

    public EditorTreeTableRow(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        contextMenu = createContextMenu();
    }

    @Override
    protected void updateItem(Pair<String, T> item, boolean empty) {
        super.updateItem(item, empty);
        TreeItem treeItem = getTreeItem();
        if (treeItem != null && treeItem.getChildren().isEmpty()) {
            setContextMenu(null);
        } else {
            setContextMenu(contextMenu);
        }
    }

    private ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem expandAllItems = new MenuItem(resourceBundle.getString("expand_all"));
        MenuItem collapseAllItems = new MenuItem(resourceBundle.getString("collapse_all"));
        MenuItem separatorItem = new SeparatorMenuItem();
        MenuItem expandAllChildrenItems = new MenuItem(resourceBundle.getString("expand_all_children"));
        MenuItem collapseAllChildrenItems = new MenuItem(resourceBundle.getString("collapse_all_children"));

        expandAllItems.setOnAction(event -> {
            TreeItem<Pair<String, T>> item = getTreeItem();
            // fixme: If a crash occur here uncomment below
            // if (item == null) return;

            item.setExpanded(true);
            expandAllChildren(item, true);
            event.consume();
        });
        collapseAllItems.setOnAction(event -> {
            TreeItem<Pair<String, T>> item = getTreeItem();
            item.setExpanded(false);
            expandAllChildren(item, false);
            event.consume();
        });
        expandAllChildrenItems.setOnAction(event -> {
            TreeItem<Pair<String, T>> item = getTreeItem();
            expandAllChildren(item, true);
            event.consume();
        });
        collapseAllChildrenItems.setOnAction(event -> {
            TreeItem<Pair<String, T>> item = getTreeItem();
            expandAllChildren(item, false);
            event.consume();
        });

        contextMenu.getItems().addAll(expandAllItems, collapseAllItems, separatorItem,
                expandAllChildrenItems, collapseAllChildrenItems);

        return contextMenu;
    }

    private static <T> void expandAllChildren(TreeItem<Pair<String, T>> item, boolean expand) {
        item.getChildren().forEach(o -> {
            if (!o.getChildren().isEmpty()) {
                o.setExpanded(expand);
                expandAllChildren(o, expand);
            } else {
                o.setExpanded(!expand);
            }
        });
    }

}
