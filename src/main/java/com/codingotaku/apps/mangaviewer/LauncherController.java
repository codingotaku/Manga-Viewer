package com.codingotaku.apps.mangaviewer;

import java.util.stream.Stream;

import com.codingotaku.api.jmanga.manga.Manga;
import com.codingotaku.apps.mangaviewer.custom.MangaDetails;
import com.codingotaku.apps.mangaviewer.custom.MangaInfo;
import com.codingotaku.apps.mangaviewer.helper.Server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LauncherController {
	@FXML private VBox slideBar;
	@FXML private ListView<MangaInfo> mangaList;
	@FXML private Pane detailPane;
	@FXML private TextField search;
	@FXML private Label minimize;
	@FXML private Label resize;
	@FXML private Label close;
	@FXML private HBox title;
	@FXML private HBox center;

	private final Delta dragDelta = new Delta();// for title bar dragging
	private final ObservableList<MangaInfo> observableList = FXCollections.observableArrayList();
	private Stage stage;

	@FXML
	private void initialize() {
		mangaList.setItems(observableList);
		Server.getGetMangaList(list -> list.getMangas().forEach(manga -> observableList.add(new MangaInfo(manga))));

		search.textProperty().addListener((observable, oldVal, newVal) -> {
			if (newVal != null) {
				observableList.clear();
				String[] words = newVal.toLowerCase().split("[\\s]");
				Server.getGetMangaList(list -> {
					Stream<Manga> filter = list.getMangas()
							.stream()
							.filter(manga -> {
								boolean found = true;
								for (String word : words) {
									if (!manga.getTitle().toLowerCase().contains(word)) {
										found = false;
										break;
									}
								}
								return found;
							});
					filter.forEach(manga -> observableList.add(new MangaInfo(manga)));
				});
			}
		});
		mangaList.getSelectionModel()
				.selectedItemProperty()
				.addListener((list, oldVal, newVal) -> {
					if (newVal != null) { // Safety check
						detailPane.getChildren().setAll(new MangaDetails(newVal.getManga()));
					}
				});
	}

	@FXML
	void loadProfile() {

	}

	@FXML
	private void resize() {
		if (stage == null) stage = (Stage) close.getScene().getWindow(); // an ugly way of initializing stage
		if (stage.isMaximized()) {
			stage.setMaximized(false);
			resize.setText("⬜");
		} else {
			stage.setY(0);
			stage.setMaximized(true);
			resize.setText("⧉");
		}
	}

	@FXML
	private void titleSelected(MouseEvent event) {
		if (stage == null) stage = (Stage) title.getScene().getWindow();
		dragDelta.x = event.getScreenX() - stage.getX();
		dragDelta.y = event.getScreenY() - stage.getY();
	}

	@FXML
	private void titleDragged(MouseEvent event) {
		if (stage.isMaximized()) {
			double pw = stage.getWidth();
			resize();
			double nw = stage.getWidth();
			dragDelta.x /= (pw / nw);
		}
		stage.setX(event.getScreenX() - dragDelta.x);
		stage.setY(event.getScreenY() - dragDelta.y);
	}

	@FXML
	private void titleReleased(MouseEvent event) {
		if (event.getScreenY() == 0 && !stage.isMaximized()) {
			resize();
		}
	}

	@FXML
	private void minimize() {
		if (stage == null) stage = (Stage) title.getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	private void close() {
		if (stage == null) stage = (Stage) title.getScene().getWindow();
		stage.close();
	}

	private class Delta {
		double x, y;
	}
}
