package com.codingotaku.apps.mangaviewer;

import java.util.stream.Stream;

import com.codingotaku.api.jmanga.manga.Manga;
import com.codingotaku.apps.mangaviewer.custom.MangaDetails;
import com.codingotaku.apps.mangaviewer.custom.MangaInfo;
import com.codingotaku.apps.mangaviewer.helper.Server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class LauncherController {
	@FXML VBox slideBar;
	@FXML ListView<MangaInfo> mangaList;
	@FXML Pane detailPane;
	@FXML TextField search;

	ObservableList<MangaInfo> observableList = FXCollections.observableArrayList();

	@FXML
	void initialize() {
		mangaList.setItems(observableList);
		Server.getGetMangaList(list -> {
			list.getMangas().forEach(manga -> {
				observableList.add(new MangaInfo(manga));
			});
		});

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
					filter.forEach(manga -> {
						observableList.add(new MangaInfo(manga));
					});
				});
			}
		});
		mangaList.getSelectionModel()
				.selectedItemProperty().addListener((list, oldVal, newVal) -> {
					if (newVal != null) {// Safety check
						detailPane.getChildren().setAll(new MangaDetails(newVal.getManga()));
					}
				});
	}
}
