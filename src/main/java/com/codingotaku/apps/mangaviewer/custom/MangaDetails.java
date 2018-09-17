package com.codingotaku.apps.mangaviewer.custom;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

import com.codingotaku.api.jmanga.manga.Manga;
import com.codingotaku.api.jmanga.manga.MangaInfo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class MangaDetails extends GridPane {

	private String getCategories(String[] array) {
		StringBuffer builder = new StringBuffer("Categories : ");
		if (array.length == 0) {
			builder.append("N/A");
			return builder.toString();
		}

		for (String category : array) {
			builder.append(category);
			builder.append(", ");
		}

		// Remove last comma
		builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}

	private String getAlias(String[] array) {
		StringBuffer builder = new StringBuffer("Alias\n");
		if (array.length == 0) {
			builder.append("N/A");
			return builder.toString();
		}

		for (String alias : array) {
			builder.append(StringEscapeUtils.unescapeHtml(alias));
			builder.append(", \n");
		}

		// Remove last comma
		builder.delete(builder.length() - 2, builder.length());
		return builder.toString();
	}

	private ObservableList<ChapterDetails> observableList = FXCollections.observableArrayList();

	public MangaDetails(Manga manga) {
		setMinWidth(300);
		setPadding(new Insets(5, 50, 50, 50));
		setHgap(10);
		setVgap(10);
		setMaxHeight(800);

		ImageView poster = new ImageView();
		poster.setFitHeight(280);
		poster.setFitWidth(180);

		Label hits = new Label("Hits : " + String.valueOf(manga.getHits()));
		Label status = new Label("Status : " + (manga.getStatus() == 1 ? "In Progress" : "Completed"));
		Label chapters = new Label();
		Label categories = new Label(getCategories(manga.getCategories()));
		TextArea description = new TextArea("Loading description");
		Label title = new Label(manga.getTitle());
		Label author = new Label();
		Label alias = new Label();
		ListView<ChapterDetails> list = new ListView<ChapterDetails>();
		ScrollPane scrollPane = new ScrollPane(list);
		ScrollPane aliasPane = new ScrollPane(alias);
		HBox lists = new HBox();
		Button readFromStart = new Button("Read from start");
		Button continueRead = new Button("Continue read");
		aliasPane.setMinWidth(200);
		lists.setMinWidth(400);
		lists.getChildren().addAll(aliasPane, scrollPane);

		GridPane.setConstraints(poster, 0, 0, 3, 5);
		GridPane.setConstraints(hits, 4, 0);
		GridPane.setConstraints(status, 5, 0);
		GridPane.setConstraints(chapters, 6, 0);
		GridPane.setConstraints(categories, 4, 1, 4, 1);
		GridPane.setConstraints(description, 3, 2, 4, 3);
		GridPane.setConstraints(title, 0, 6);
		GridPane.setConstraints(author, 3, 6);
		GridPane.setConstraints(readFromStart, 4, 6, 2, 1);
		GridPane.setConstraints(continueRead, 6, 6);
		GridPane.setConstraints(lists, 0, 7, 8, 1);

		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		description.setEditable(false);
		description.setWrapText(true);
		list.setItems(observableList);

		readFromStart.setOnAction(event -> {
			new ReadDialog(observableList, 0);
		});
		getChildren().addAll(poster, hits, status, chapters, categories, description, title, author,
				lists, readFromStart, continueRead);

		new Thread(() -> {
			Image image;
			if (manga.getImage() != null && !manga.getImage().endsWith("/null"))
				image = new Image(manga.getImage());
			else
				image = new Image(getClass().getClassLoader().getResourceAsStream("images/panda.png"));

			poster.setImage(image);

			try {
				MangaInfo info = manga.getMangaInfo();
				Platform.runLater(() -> {
					String text = info.getDescription();
					description.setText(text.isEmpty() ? "N/A" : text);
					author.setText("Author - " + info.getAuthor());
					alias.setText(getAlias(info.getAka()));
					chapters.setText("Chapters - " + info.getChapters_len());
					info.getChapters().forEach(chapter -> {
						observableList.add(new ChapterDetails(chapter));
					});
				});
			} catch (IOException e) {
				// TODO: Handle network exception
				e.printStackTrace();
			}
		}).start();
	}
}