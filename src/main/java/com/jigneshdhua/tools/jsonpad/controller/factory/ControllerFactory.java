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
package com.jigneshdhua.tools.jsonpad.controller.factory;

import java.lang.reflect.InvocationTargetException;

import com.jigneshdhua.tools.jsonpad.controller.ui.EditorTabController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Callback;

public final class ControllerFactory implements Callback<Class<?>, Object> {

    private final Application application;
    private final Stage stage;

   public ControllerFactory(Application application, Stage stage) {
        this.application = application;
        this.stage = stage;
    }

   

    @Override
    public Object call(Class<?> type) {
        try {
            if (EditorTabController.class.isAssignableFrom(type)) {
                // EditorTabController is final so we don't need to guess the ctor
                return new EditorTabController(application, stage);
            } else if (StageController.class.isAssignableFrom(type)) {
                // Call the class first ctor
                return type.getConstructors()[0].newInstance(application, stage);
            } else if (Controller.class.isAssignableFrom(type)) {
                // Call the class first ctor
                return type.getConstructors()[0].newInstance(application);
            }
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("No constructor found");
    }

}
