package com.codingotaku.apps.mangaviewer.custom;

import com.codingotaku.api.jmanga.manga.ChapterInfo;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ChapterDetails extends GridPane {
	private final ChapterInfo chapter;
	private final String title;

	public ChapterDetails(ChapterInfo chapter) {
		setPadding(new Insets(5, 50, 5, 50));
		setHgap(10);
		setVgap(10);
		Label title;
		if (chapter.getTitle() == null
				|| chapter.getTitle().equals("null")
				|| chapter.getTitle().equals(String.valueOf(chapter.getChapterNumber()))) {
			title = new Label(String.valueOf(chapter.getChapterNumber()));
		} else {
			title = new Label(chapter.getChapterNumber() + " " + chapter.getTitle());
		}
		this.title = title.getText();
		this.chapter = chapter;
		getChildren().addAll(title);
	}

	public ChapterInfo getChapter() {
		return chapter;
	}

	public String getTitle() {
		return title;
	}
}
