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

import javafx.application.Application;
import javafx.stage.Stage;

public class StageController extends Controller {

    private final Stage stage;

    public StageController(Application application, Stage stage) {
        super(application);
        this.stage = stage;
    }

    protected Stage getStage() {
        return stage;
    }

}
