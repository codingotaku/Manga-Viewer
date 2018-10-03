package com.codingotaku.apps.mangaviewer.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.codingotaku.api.jmanga.JEdenManga;
import com.codingotaku.api.jmanga.manga.MangaList;
import com.google.gson.Gson;

public class Server {
	private static Server server = null;
	private MangaList list = null;

	public static void getGetMangaList(Callback callback) {
		if (server != null) {
			callback.loaded(server.list);
		} else {
			new Thread(() -> {
				server = new Server();
				callback.loaded(server.list);
			}).start();
		}
	}

	private Server() {
		JEdenManga api = new JEdenManga();
		Gson gson = new Gson();
		File file = new File("properties.json");
	
		try {
			if (file.exists() && (System.currentTimeMillis() - file.lastModified()) < 86400000) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				list = gson.fromJson(reader, MangaList.class);
				reader.close();
			} else {
				list = api.getAllManga();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				gson.toJson(list, writer);
				writer.close();
			}
		} catch (IOException e) {
			// TODO: handle network exception
			e.printStackTrace();
		}
	}
}
