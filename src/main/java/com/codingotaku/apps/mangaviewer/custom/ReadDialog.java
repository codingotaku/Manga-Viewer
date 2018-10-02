package com.codingotaku.apps.mangaviewer.custom;

import java.io.IOException;
import java.util.ArrayList;

import com.codingotaku.api.jmanga.manga.ChapterInfo;
import com.codingotaku.api.jmanga.manga.Page;
import com.codingotaku.apps.mangaviewer.helper.Toast;
import com.codingotaku.apps.mangaviewer.helper.ZoomScrollPane;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	private boolean isLoading = false;

	ReadDialog(ObservableList<ChapterDetails> chapters, int index) {
		final Stage dialog = new Stage();
		final Scene scene = new Scene(view, 1200, 600);
		final Image next = new Image(getClass().getClassLoader().getResourceAsStream("images/next.png"));
		final Image previous = new Image(getClass().getClassLoader().getResourceAsStream("images/previous.png"));

		this.chapters = chapters;
		this.chapterIndex = index;

		chapterIndexPrev = chapterIndex;
		dialog.initModality(Modality.WINDOW_MODAL);
		imgView.setPreserveRatio(true);

		chapter = chapters.get(index).getChapter();
		loadNewChapter(dialog);
		imgView.setFocusTraversable(true);
		imgView.requestFocus();

		String exitHint = "Press Ctrl+F or Esc to exit fullscreen";
		String travelHint = "Ctrl+Left/Right for next/previous page";
		dialog.setFullScreenExitHint(String.format("%s\n\n%s", exitHint, travelHint));
		dialog.setFullScreen(true);
		scene.getStylesheets()
				.add(getClass().getClassLoader().getResource("css/style.css").toExternalForm());

		view.setOnMouseMoved(event -> {
			event.consume();
			if (event.getSceneX() >= view.getWidth() - 100) {
				scene.setCursor(new ImageCursor(next));
			} else if (event.getSceneX() <= 100) {
				scene.setCursor(new ImageCursor(previous));
			} else {
				scene.setCursor(Cursor.DEFAULT);
			}
		});

		view.setOnMouseClicked(event -> {
			event.consume();
			if (event.getSceneX() > view.getWidth() - 100) {
				nextChapter(dialog);
			} else if (event.getSceneX() < 100) {
				prevChapter(dialog);
			}
		});

		view.setOnKeyReleased(event -> {
			event.consume();
			if (event.isControlDown()) {
				switch (event.getCode()) {
				case RIGHT:
					nextChapter(dialog);
					break;
				case LEFT:
					prevChapter(dialog);
					break;
				case F:
					dialog.setFullScreen(false);
					break;
				default:
					// do nothing
					break;
				}
			}
		});
		dialog.setScene(scene);
		dialog.show();
	}

	private void prevChapter(Stage stage) {
		if (pageIndex < pages.size() - 1) {
			pageIndex++;
			Toast.makeText(stage, "Loading page " + (pages.size() - pageIndex));
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
			new Thread(() -> {
				imgView.setImage(new Image(pages.get(pageIndex).getImageUrl()));
				view.setVvalue(0.0);
			}).start();

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

	private void loadNewChapter(Stage stage) {
		chapter = chapters.get(chapterIndex).getChapter();
		Platform.runLater(() -> Toast.makeText(stage, "Loading " + chapter.getChapterNumber()));
		new Thread(() -> {
			isLoading = true;
			try {
				pages = chapter.getPages();
				pageIndex = pages.size() - 1;
				imgView.setImage(new Image(pages.get(pageIndex).getImageUrl()));
				view.setVvalue(0.0);
				Platform.runLater(() -> Toast.makeText(stage, "Reading " + chapter.getChapterNumber() + " page 0"));
			} catch (IOException e) {
				chapterIndex = chapterIndexPrev;
				Platform.runLater(() -> Toast.makeText(stage, "Failed to load " + chapter.getChapterNumber()));
			}
			isLoading = false;
		}).start();
	}
}
