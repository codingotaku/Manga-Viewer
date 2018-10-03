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
	private final ArrayList<Image> images;
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
		images = new ArrayList<Image>();

		String exitHint = "Press Ctrl+F or Esc to exit fullscreen";
		dialog.setFullScreenExitHint(exitHint);
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
				nextPage(dialog);
			} else if (event.getSceneX() < 100) {
				prevPage(dialog);
			}
		});

		view.setOnKeyReleased(event -> {
			event.consume();
			if (event.isControlDown()) {
				switch (event.getCode()) {
				case RIGHT:
					nextPage(dialog);
					break;
				case LEFT:
					prevPage(dialog);
					break;
				case F:
					dialog.setFullScreen(!dialog.isFullScreen());
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

	private void prevPage(Stage stage) {
		if (pageIndex > 0) {
			pageIndex--;
			Toast.makeText(stage, "Loading page " + pageIndex);
			new Thread(() -> loadPage(pageIndex)).start();
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

	private void nextPage(Stage stage) {
		if (pageIndex < pages.size() - 2) {
			pageIndex++;
			Toast.makeText(stage, "Loading page " + pageIndex);
			new Thread(() -> loadPage(pageIndex)).start();
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

	private void loadPage(int pageIndex) {
		Platform.runLater(() -> {
			imgView.setImage(images.get(pageIndex));
			if (pageIndex < pages.size() - 2) {
				if (pageIndex == images.size() - 1) {
					images.add(new Image(pages.get(pages.size() - pageIndex - 1).getImageUrl()));
				}
			}
			view.setVvalue(0.0);
		});
	}

	private void loadNewChapter(Stage stage) {
		chapter = chapters.get(chapterIndex).getChapter();
		String chapterNumber = chapter.getChapterNumber();
		Platform.runLater(() -> Toast.makeText(stage, "Loading Chapter " + chapterNumber));

		new Thread(() -> {
			isLoading = true;
			try {
				pages = chapter.getPages();
				images.clear();
				pageIndex = 0;
				images.add(new Image(pages.get(pageIndex).getImageUrl()));
				loadPage(pageIndex);
				String message = String.format("Reading Chapter %s page 0", chapterNumber);
				Platform.runLater(() -> Toast.makeText(stage, message));
			} catch (IOException e) {
				chapterIndex = chapterIndexPrev;
				Platform.runLater(() -> Toast.makeText(stage, "Failed to load chapter " + chapterNumber));
			}
			isLoading = false;
		}).start();
	}
}
