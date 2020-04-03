/*
 * Copyright (C) 2010-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.libraryService;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.FileObserver;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.android.fbreader.httpd.DataService;
import org.geometerplus.android.fbreader.httpd.DataUtil;
import org.geometerplus.fbreader.Paths;
import org.geometerplus.fbreader.book.Author;
import org.geometerplus.fbreader.book.BookCollection;
import org.geometerplus.fbreader.book.BookEvent;
import org.geometerplus.fbreader.book.BookUtil;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.book.BooksDatabase;
import org.geometerplus.fbreader.book.DbBook;
import org.geometerplus.fbreader.book.IBookCollection;
import org.geometerplus.fbreader.book.SerializerUtil;
import org.geometerplus.fbreader.book.Tag;
import org.geometerplus.fbreader.book.UID;
import org.geometerplus.zlibrary.core.options.Config;
import org.geometerplus.zlibrary.text.view.ZLTextFixedPosition;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LibraryService extends Service {
	private static SQLiteBooksDatabase ourDatabase;
	private static final Object ourDatabaseLock = new Object();

	final DataService.Connection DataConnection = new DataService.Connection();

	/**
	 * author:WuZepeng </br>
	 * time:2017-09-21 14:14 </br>
	 * desc:文件观察类，观察文件的各种事件：移动、删除等事件 </br>
	 */
	private static final class Observer extends FileObserver {
		private static final int MASK =
			MOVE_SELF | MOVED_TO | MOVED_FROM | DELETE_SELF | DELETE | CLOSE_WRITE | ATTRIB;

		private final String myPrefix;
		private final BookCollection myCollection;

		public Observer(String path, BookCollection collection) {
			super(path, MASK);
			myPrefix = path + '/';
			myCollection = collection;
			Logger.e("observer path:%s%n",path);
		}

		@Override
		public void onEvent(int event, String path) {
			event = event & ALL_EVENTS;
			System.err.println("Event " + event + " on " + path);
			switch (event) {
				case MOVE_SELF:
					// TODO: File(path) removed; stop watching (?)
					break;
				case MOVED_TO:
					myCollection.rescan(myPrefix + path);
					break;
				case MOVED_FROM:
				case DELETE:
					myCollection.rescan(myPrefix + path);
					break;
				case DELETE_SELF:
					// TODO: File(path) removed; watching is stopped automatically (?)
					break;
				case CLOSE_WRITE:
				case ATTRIB:
					myCollection.rescan(myPrefix + path);
					break;
				default:
					System.err.println("Unexpected event " + event + " on " + myPrefix + path);
					break;
			}
		}
	}

	public final class LibraryImplementation extends LibraryInterface.Stub {
		private final BooksDatabase myDatabase;
		private final List<FileObserver> myFileObservers = new LinkedList<FileObserver>();
		private BookCollection myCollection;

		LibraryImplementation(BooksDatabase db) {
			myDatabase = db;
			myCollection = new BookCollection(
				Paths.systemInfo(LibraryService.this), myDatabase, Paths.bookPath()
			);
//			Logger.e("wzp LibraryImplementation");
			reset(true);
		}

		@Override
		public void reset(final boolean force) {
			Config.Instance().runOnConnect(new Runnable() {
				public void run() {
					resetInternal(force);
				}
			});
		}

		private void resetInternal(boolean force) {
			final List<String> bookDirectories = Paths.bookPath();
//			Logger.e("wzp force:"+force);
//			for(String str:bookDirectories){
//				Logger.e("wzp bookDirectoriy:%s%n",str);
//			}
			if (!force &&
				myCollection.status() != BookCollection.Status.NotStarted &&
				bookDirectories.equals(myCollection.BookDirectories)
			) {
				return;
			}

			deactivate();
			myFileObservers.clear();

			myCollection = new BookCollection(
				Paths.systemInfo(LibraryService.this), myDatabase, bookDirectories
			);
			for (String dir : bookDirectories) {
				final Observer observer = new Observer(dir, myCollection);
				observer.startWatching();
				myFileObservers.add(observer);
			}

			myCollection.addListener(new BookCollection.Listener<DbBook>() {
				@Override
				public void onBookEvent(BookEvent event, DbBook book) {
					final Intent intent = new Intent(FBReaderIntents.Event.LIBRARY_BOOK);
					intent.putExtra("type", event.toString());
					intent.putExtra("book", SerializerUtil.serialize(book));
//					Logger.e("wzp book:%s%n",book.getTitle());
					sendBroadcast(intent,FBReaderIntents.Event.CUSTOM_BROADCAST_PSERMISSION);
				}
				@Override
				public void onBuildEvent(BookCollection.Status status) {
					final Intent intent = new Intent(FBReaderIntents.Event.LIBRARY_BUILD);
					intent.putExtra("type", status.toString());
					sendBroadcast(intent,FBReaderIntents.Event.CUSTOM_BROADCAST_PSERMISSION);
				}
			});
			myCollection.startBuild();
		}

		public void deactivate() {
			for (FileObserver observer : myFileObservers) {
				observer.stopWatching();
			}
		}
		@Override
		public String status() {
			return myCollection.status().toString();
		}
		@Override
		public int size() {
			return myCollection.size();
		}
		@Override
		public List<String> books(String query) {
			return SerializerUtil.serializeBookList(
				myCollection.books(SerializerUtil.deserializeBookQuery(query))
			);
		}
		@Override
		public boolean hasBooks(String query) {
			return myCollection.hasBooks(SerializerUtil.deserializeBookQuery(query).Filter);
		}

		public long getBookIdByTitleAndLanguage(int languageType,String bookName){
			return myCollection.getBookIdByTitleAndLanguage(languageType,bookName);
		}
		@Override
		public List<String> recentBooks() {
			return recentlyOpenedBooks(12);
		}
		@Override
		public List<String> recentlyOpenedBooks(int count) {
			return SerializerUtil.serializeBookList(myCollection.recentlyOpenedBooks(count));
		}
		@Override
		public List<String> recentlyAddedBooks(int count) {
			return SerializerUtil.serializeBookList(myCollection.recentlyAddedBooks(count));
		}
		@Override
		public String getRecentBook(int index) {
			return SerializerUtil.serialize(myCollection.getRecentBook(index));
		}
		@Override
		public String getBookByFile(String path) {
			return SerializerUtil.serialize(myCollection.getBookByFile(path));
		}
		@Override
		public String getBookById(long id) {
			return SerializerUtil.serialize(myCollection.getBookById(id));
		}
		@Override
		public String getBookByUid(String type, String id) {
			return SerializerUtil.serialize(myCollection.getBookByUid(new UID(type, id)));
		}
		@Override
		public String getBookByHash(String hash) {
			return SerializerUtil.serialize(myCollection.getBookByHash(hash));
		}
		@Override
		public List<String> authors() {
			final List<Author> authors = myCollection.authors();
			final List<String> strings = new ArrayList<String>(authors.size());
			for (Author a : authors) {
				strings.add(Util.authorToString(a));
			}
			return strings;
		}
		@Override
		public boolean hasSeries() {
			return myCollection.hasSeries();
		}
		@Override
		public List<String> series() {
			return myCollection.series();
		}
		@Override
		public List<String> tags() {
			final List<Tag> tags = myCollection.tags();
			final List<String> strings = new ArrayList<String>(tags.size());
			for (Tag t : tags) {
				strings.add(Util.tagToString(t));
			}
			return strings;
		}
		@Override
		public List<String> titles(String query) {
			return myCollection.titles(SerializerUtil.deserializeBookQuery(query));
		}
		@Override
		public List<String> firstTitleLetters() {
			return myCollection.firstTitleLetters();
		}
		@Override
		public boolean saveBook(String book) {
			return myCollection.saveBook(SerializerUtil.deserializeBook(book, myCollection));
		}
		@Override
		public boolean canRemoveBook(String book, boolean deleteFromDisk) {
			return myCollection.canRemoveBook(SerializerUtil.deserializeBook(book, myCollection), deleteFromDisk);
		}
		@Override
		public void removeBook(String book, boolean deleteFromDisk) {
			myCollection.removeBook(SerializerUtil.deserializeBook(book, myCollection), deleteFromDisk);
		}
		@Override
		public void addToRecentlyOpened(String book) {
			myCollection.addToRecentlyOpened(SerializerUtil.deserializeBook(book, myCollection));
		}
		@Override
		public void removeFromRecentlyOpened(String book) {
			myCollection.removeFromRecentlyOpened(SerializerUtil.deserializeBook(book, myCollection));
		}
		@Override
		public List<String> labels() {
			return myCollection.labels();
		}
		@Override
		public PositionWithTimestamp getStoredPosition(long bookId) {
			final ZLTextPosition position = myCollection.getStoredPosition(bookId);
			return position != null ? new PositionWithTimestamp(position) : null;
		}
		@Override
		public void storePosition(long bookId, PositionWithTimestamp pos) {
			if (pos == null) {
				return;
			}
			myCollection.storePosition(bookId, new ZLTextFixedPosition.WithTimestamp(
				pos.ParagraphIndex, pos.ElementIndex, pos.CharIndex, pos.Timestamp
			));
		}

		@Override
		public boolean isHyperlinkVisited(String book, String linkId) {
			return myCollection.isHyperlinkVisited(SerializerUtil.deserializeBook(book, myCollection), linkId);
		}

		@Override
		public void markHyperlinkAsVisited(String book, String linkId) {
			myCollection.markHyperlinkAsVisited(SerializerUtil.deserializeBook(book, myCollection), linkId);
		}

		@Override
		public String getCoverUrl(String path) {
			return DataUtil.buildUrl(DataConnection, "cover", path);
		}

		@Override
		public String getDescription(String book) {
			return BookUtil.getAnnotation(SerializerUtil.deserializeBook(book, myCollection), myCollection.PluginCollection);
		}

		@Override
		public Bitmap getCover(final String bookString, final int maxWidth, final int maxHeight, boolean[] delayed) {
			// this method kept for compatibility
			delayed[0] = false;
			return null;
		}

		private Bitmap getResizedBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
			if (maxWidth <= 0 || maxHeight <= 0) {
				return null;
			}

			final int bWidth = bitmap.getWidth();
			final int bHeight = bitmap.getHeight();
			if (maxWidth > bWidth && maxHeight > bHeight) {
				return null;
			}

			final int w, h;
			if (bWidth * maxHeight > bHeight * maxWidth) {
				w = maxWidth;
				h = Math.max(1, (int)(bHeight * (w + .5f) / bWidth));
			} else {
				h = maxHeight;
				w = Math.max(1, (int)(bWidth * (h + .5f) / bHeight));
			}
			if (2 * w <= bWidth && 2 * h <= bHeight) {
				return bitmap;
			}
			return Bitmap.createScaledBitmap(bitmap, w, h, false);
		}
		@Override
		public List<String> bookmarks(String query) {
			return SerializerUtil.serializeBookmarkList(myCollection.bookmarks(
				SerializerUtil.deserializeBookmarkQuery(query, myCollection)
			));
		}
		@Override
		public String saveBookmark(String serialized) {
			final Bookmark bookmark = SerializerUtil.deserializeBookmark(serialized);
			myCollection.saveBookmark(bookmark);
			return SerializerUtil.serialize(bookmark);
		}
		@Override
		public void deleteBookmark(String serialized) {
			myCollection.deleteBookmark(SerializerUtil.deserializeBookmark(serialized));
		}
		@Override
		public List<String> deletedBookmarkUids() {
			return myCollection.deletedBookmarkUids();
		}
		@Override
		public void purgeBookmarks(List<String> uids) {
			myCollection.purgeBookmarks(uids);
		}
		@Override
		public String getHighlightingStyle(int styleId) {
			return SerializerUtil.serialize(myCollection.getHighlightingStyle(styleId));
		}
		@Override
		public List<String> highlightingStyles() {
			return SerializerUtil.serializeStyleList(myCollection.highlightingStyles());
		}
		@Override
		public void saveHighlightingStyle(String style) {
			myCollection.saveHighlightingStyle(SerializerUtil.deserializeStyle(style));
		}
		@Override
		public int getDefaultHighlightingStyleId() {
			return myCollection.getDefaultHighlightingStyleId();
		}
		@Override
		public void setDefaultHighlightingStyleId(int styleId) {
			myCollection.setDefaultHighlightingStyleId(styleId);
		}
		@Override
		public void rescan(String path) {
			myCollection.rescan(path);
		}
		@Override
		public String getHash(String book, boolean force) {
			return myCollection.getHash(SerializerUtil.deserializeBook(book, myCollection), force);
		}
		@Override
		public void setHash(String book, String hash) {
			myCollection.setHash(SerializerUtil.deserializeBook(book, myCollection), hash);
		}
		@Override
		public List<String> formats() {
			final List<IBookCollection.FormatDescriptor> descriptors = myCollection.formats();
			final List<String> serialized = new ArrayList<String>(descriptors.size());
			for (IBookCollection.FormatDescriptor d : descriptors) {
				serialized.add(Util.formatDescriptorToString(d));
			}
			return serialized;
		}
		@Override
		public boolean setActiveFormats(List<String> formatIds) {
			if (myCollection.setActiveFormats(formatIds)) {
				reset(true);
				return true;
			} else {
				return false;
			}
		}
	}

	private volatile LibraryImplementation myLibrary;

	@Override
	public void onStart(Intent intent, int startId) {
		onStartCommand(intent, 0, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myLibrary;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		synchronized (ourDatabaseLock) {
			if (ourDatabase == null) {
				ourDatabase = new SQLiteBooksDatabase(LibraryService.this);
//				Logger.e("wzp 初始化数据库");
			}
		}
//		Logger.e("wzp libraryService");
		myLibrary = new LibraryImplementation(ourDatabase);

		bindService(
			new Intent(this, DataService.class),
			DataConnection,
			DataService.BIND_AUTO_CREATE
		);
	}

	@Override
	public void onDestroy() {
		unbindService(DataConnection);

		if (myLibrary != null) {
			final LibraryImplementation l = myLibrary;
			myLibrary = null;
			l.deactivate();
		}
		super.onDestroy();
	}
}
