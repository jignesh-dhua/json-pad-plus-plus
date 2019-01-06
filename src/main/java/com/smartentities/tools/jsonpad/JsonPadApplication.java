package com.smartentities.tools.jsonpad;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 
 * @author Jigneshkumar Dhua
 *
 */
public class JsonPadApplication extends Application {

	@Override
	public void start(Stage stage) {
		Label l = new Label("Hello, JsonPad++");
		Scene scene = new Scene(new StackPane(l), 640, 480);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}