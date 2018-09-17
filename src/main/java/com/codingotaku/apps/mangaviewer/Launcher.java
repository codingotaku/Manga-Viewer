package com.codingotaku.apps.mangaviewer;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Launcher extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setMinWidth(1000.0);
		primaryStage.setMinHeight(500.0);

		primaryStage.setWidth(1000.0);
		primaryStage.setHeight(500.0);

		HBox root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/Launcher.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
		primaryStage.setTitle("Manga Viewer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
