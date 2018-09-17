package com.codingotaku.apps.mangaviewer.custom;

import com.codingotaku.api.jmanga.manga.Manga;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class MangaInfo extends GridPane {
	private final Manga manga;

	public Manga getManga() {
		return manga;
	}

	public MangaInfo(Manga info) {
		manga = info;
		Label title = new Label(info.getTitle());
		Label status = new Label(info.getStatus() == 1 ? "In Progress" : "Completed");
		Label hits = new Label("Hits " + String.valueOf(info.getHits()));
		setPadding(new Insets(5, 5, 5, 5));
		setHgap(10);
		setVgap(10);
		GridPane.setConstraints(title, 0, 0, 4, 1);
		GridPane.setConstraints(hits, 0, 1);
		GridPane.setConstraints(new Label(), 1, 1);
		GridPane.setConstraints(status, 2, 1);

		getChildren().addAll(title, status, hits);
	}
}