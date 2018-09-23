package com.codingotaku.apps.mangaviewer.custom;

import java.io.IOException;
import java.util.ArrayList;

import com.codingotaku.api.jmanga.manga.ChapterInfo;
import com.codingotaku.api.jmanga.manga.Page;
import com.codingotaku.apps.mangaviewer.helper.Toast;
import com.codingotaku.apps.mangaviewer.helper.ZoomScrollPane;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReadDialog {
	private final ImageView imgView = new ImageView();
	private final ZoomScrollPane view = new ZoomScrollPane(imgView);
	private final ObservableList<ChapterDetails> chapters;
	
	private ArrayList<Page> pages;
	private ChapterInfo chapter = null;
	
	private int pageIndex = 0;
	private int chapterIndex;
	private int chapterIndexPrev;

	ReadDialog(ObservableList<ChapterDetails> chapters, int index) {
		final Stage dialog = new Stage();
		this.chapters = chapters;
		this.chapterIndex = index;
		chapterIndexPrev = chapterIndex;
		dialog.initModality(Modality.WINDOW_MODAL);
		imgView.setPreserveRatio(true);

		chapter = chapters.get(index).getChapter();
		loadNewChapter(dialog);
		imgView.setFocusTraversable(true);
		imgView.requestFocus();

		view.setOnMouseClicked(e -> {
			if (e.getSceneX() > view.getWidth() - 100) {
				nextChapter(dialog);
			} else if (e.getSceneX() < 100) {
				prevChapter(dialog);
			}
		});

		Scene scene = new Scene(view, 1200, 600);
		dialog.setFullScreen(true);
		scene.getStylesheets()
				.add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
		view.setOnKeyReleased(event -> {
			if (event.isControlDown()) {
				if (event.getCode() == KeyCode.RIGHT) {
					nextChapter(dialog);
				} else if (event.getCode() == KeyCode.LEFT) {
					prevChapter(dialog);
				} else if (event.getCode() == KeyCode.F) {
					dialog.setFullScreen(!dialog.isFullScreen());
				}
			}

		});
		dialog.setScene(scene);
		dialog.show();
	}

	private void prevChapter(Stage stage) {
		if (pageIndex < pages.size() - 1) {
			pageIndex++;
			Toast.makeText(stage, "Reading page " + (pages.size() - pageIndex));
			imgView.setImage(new Image(pages.get(pageIndex).getImageUrl()));
			view.setVvalue(0.0);
		} else if (chapterIndex < chapters.size() - 1) {
			if (!isLoading) {
				chapterIndexPrev = chapterIndex;
				chapterIndex++;
				loadNewChapter(stage);
			}
		} else {
			Toast.makeText(stage, "No more chapters!");
		}
	}

	private void nextChapter(Stage stage) {
		if (pageIndex > 0) {
			pageIndex--;
			Toast.makeText(stage, "Reading page " + (pages.size() - pageIndex));
			imgView.setImage(new Image(pages.get(pageIndex).getImageUrl()));
			view.setVvalue(0.0);
		} else if (chapterIndex > 0) {
			if (!isLoading) {
				chapterIndexPrev = chapterIndex;
				chapterIndex--;
				loadNewChapter(stage);
			}
		} else {
			Toast.makeText(stage, "No more chapters!");
		}
	}

	boolean isLoading = false;

	private void loadNewChapter(Stage stage) {
		chapter = chapters.get(chapterIndex).getChapter();
		Toast.makeText(stage, "Loading " + chapter.getChapterNumber());
		new Thread(() -> {
			isLoading = true;
			try {
				pages = chapter.getPages();
				pageIndex = pages.size() - 1;
				imgView.setImage(new Image(pages.get(pageIndex).getImageUrl()));
				view.setVvalue(0.0);
				Platform.runLater(() -> {
					Toast.makeText(stage, "Reading " + chapter.getChapterNumber() + " page 0");
				});
			} catch (IOException e) {
				chapterIndex = chapterIndexPrev;
				Platform.runLater(() -> {
					Toast.makeText(stage, "Failed to load " + chapter.getChapterNumber());
				});
			}
			isLoading = false;
		}).start();
	}
}
