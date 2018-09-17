package com.codingotaku.apps.mangaviewer.helper;

import com.codingotaku.api.jmanga.manga.MangaList;

@FunctionalInterface
public interface Callback {
	void loaded(MangaList list);
}
