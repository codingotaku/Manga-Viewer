package com.codingotaku.apps.mangaviewer.custom;

import java.io.IOException;
import java.util.ArrayList;

import com.codingotaku.api.jmanga.manga.Page;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReadDialog {
	private ArrayList<Page> pages;
	private int i = 0;
	private ImageView imgView = new ImageView();
	private ScrollPane view = new ScrollPane(imgView);

	ReadDialog(ObservableList<ChapterDetails> chapters, int index) {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);

		imgView.fitWidthProperty().bind(view.widthProperty());
		imgView.setPreserveRatio(true);

		new Thread(() -> {
			try {
				pages = chapters.get(index).getChapter().getPages();
				imgView.setImage(new Image(pages.get(0).getImageUrl()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

		imgView.setOnMouseClicked(e -> {
			if (e.getSceneX() > imgView.getFitWidth() - 100) {
				nextChapter();
			}else if (e.getSceneX() < -100) {
				prevChapter();
			}
		});

		Scene dialogScene = new Scene(view, 1300, 600);
		dialogScene.getStylesheets()
				.add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
		dialog.setScene(dialogScene);
		dialog.show();
	}

	private void prevChapter() {
		if (i > 0) {
			i--;
			imgView.setImage(new Image(pages.get(i).getImageUrl()));
			view.setVvalue(0.0);
		}
	}

	private void nextChapter() {
		if (i < pages.size() - 1) {
			i++;
			imgView.setImage(new Image(pages.get(i).getImageUrl()));
			view.setVvalue(0.0);
		}
	}
}
