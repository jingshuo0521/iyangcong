/*
 * Copyright (C) 2007-2015 FBReader.ORG Limited <contact@fbreader.org>
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

package org.geometerplus.fbreader.bookmodel;

import android.util.Log;

import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.formats.BookReadingException;
import org.geometerplus.fbreader.formats.BuiltinFormatPlugin;
import org.geometerplus.fbreader.formats.FormatPlugin;
import org.geometerplus.zlibrary.core.fonts.FileInfo;
import org.geometerplus.zlibrary.core.fonts.FontEntry;
import org.geometerplus.zlibrary.core.fonts.FontManager;
import org.geometerplus.zlibrary.core.image.ZLImage;
import org.geometerplus.zlibrary.text.model.CachedCharStorage;
import org.geometerplus.zlibrary.text.model.ZLTextModel;
import org.geometerplus.zlibrary.text.model.ZLTextPlainModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class  BookModel {
	public static BookModel createModel(Book book, FormatPlugin plugin) throws BookReadingException {
		if (plugin instanceof BuiltinFormatPlugin) {
			final BookModel model = new BookModel(book);
			((BuiltinFormatPlugin)plugin).readModel(model);
			return model;
		}

		throw new BookReadingException(
			"unknownPluginType", null, new String[] { String.valueOf(plugin) }
		);
	}

	public final Book Book;
	public final TOCTree TOCTree = new TOCTree();
	public final FontManager FontManager = new FontManager();

	protected CachedCharStorage myInternalHyperlinks;
	protected final HashMap<String,ZLImage> myImageMap = new HashMap<String,ZLImage>();
	protected ZLTextModel myBookTextModel;
	protected final HashMap<String,ZLTextModel> myFootnotes = new HashMap<String,ZLTextModel>();

	public static final class Label {
		public final String ModelId;
		public final int ParagraphIndex;

		public Label(String modelId, int paragraphIndex) {
			ModelId = modelId;
			ParagraphIndex = paragraphIndex;
		}
	}

	protected BookModel(Book book) {
		Book = book;
	}

	public interface LabelResolver {
		List<String> getCandidates(String id);
	}

	private LabelResolver myResolver;

	public void setLabelResolver(LabelResolver resolver) {
		myResolver = resolver;
	}

	public Label getLabel(String id) {
		Label label = getLabelInternal(id);
		if (label == null && myResolver != null) {
			for (String candidate : myResolver.getCandidates(id)) {
				label = getLabelInternal(candidate);
				if (label != null) {
					break;
				}
			}
		}
		return label;
	}

	public void registerFontFamilyList(String[] families) {
		FontManager.index(Arrays.asList(families));
	}

	public void registerFontEntry(String family, FontEntry entry) {
		FontManager.Entries.put(family, entry);
	}

	public void registerFontEntry(String family, FileInfo normal, FileInfo bold, FileInfo italic, FileInfo boldItalic) {
		registerFontEntry(family, new FontEntry(family, normal, bold, italic, boldItalic));
	}
	//这个方法会在c++中调用。c++在解析了数据以后，通过调用这个方法将数据传给java层；
	// android-iyangcong\app\src\main\jni\NativeFormats\JavaNativeFormatPlugin.cpp中的
	// static jobject createTextModel(JNIEnv *env, jobject javaModel, ZLTextModel &model)调用此方法；
	public ZLTextModel createTextModel(
		String id, String language, int paragraphsNumber,
		int[] entryIndices, int[] entryOffsets,
		int[] paragraphLenghts, int[] textSizes, byte[] paragraphKinds,byte[] paragraphTags,
		String directoryName, String fileExtension, int blocksNumber
	) {
		return new ZLTextPlainModel(
			id, language, paragraphsNumber,
			entryIndices, entryOffsets,
			paragraphLenghts, textSizes, paragraphKinds,paragraphTags,
			directoryName, fileExtension, blocksNumber, myImageMap, FontManager
		);
	}

	public void setBookTextModel(ZLTextModel model) {
		myBookTextModel = model;
	}

	public void setFootnoteModel(ZLTextModel model) {
		myFootnotes.put(model.getId(), model);
	}

	public ZLTextModel getTextModel() {
		return myBookTextModel;
	}

	public ZLTextModel getFootnoteModel(String id) {
		return myFootnotes.get(id);
	}

	public void addImage(String id, ZLImage image) {
		myImageMap.put(id, image);
	}

	public void initInternalHyperlinks(String directoryName, String fileExtension, int blocksNumber) {
		myInternalHyperlinks = new CachedCharStorage(directoryName, fileExtension, blocksNumber);
	}

	private TOCTree myCurrentTree = TOCTree;

	public void addTOCItem(String text, int reference) {
		myCurrentTree = new TOCTree(myCurrentTree);
		myCurrentTree.setText(text);
		myCurrentTree.setReference(myBookTextModel, reference);
	}

	public void leaveTOCItem() {
		myCurrentTree = myCurrentTree.Parent;
		if (myCurrentTree == null) {
			myCurrentTree = TOCTree;
		}
	}

	private Label getLabelInternal(String id) {
		final int len = id.length();
		final int size = myInternalHyperlinks.size();

		for (int i = 0; i < size; ++i) {
			final char[] block = myInternalHyperlinks.block(i);
			for (int offset = 0; offset < block.length; ) {
				final int idLength = (int)block[offset++];
				if (idLength == 0) {
					break;
				}
				final int modelIdLength = (int)block[offset + idLength];
				if (idLength != len || !id.equals(new String(block, offset, idLength))) {
					offset += idLength + modelIdLength + 3;
					continue;
				}
				offset += idLength + 1;
				final String modelId = (modelIdLength > 0) ? new String(block, offset, modelIdLength) : null;
				offset += modelIdLength;
				final int paragraphNumber = (int)block[offset] + (((int)block[offset + 1]) << 16);
				return new Label(modelId, paragraphNumber);
			}
		}
		return null;
	}

	public String getParagraphIdByFixTrTag(int paragraphIndex){
		final int size = myInternalHyperlinks.size();
		String last = null;
		for (int i = 0; i < size; ++i) {
			final char[] block = myInternalHyperlinks.block(i);
			for (int offset = 0; offset < block.length; ) {
				//获取id长度
				final int idLength = (int)block[offset++];
				if (idLength == 0) {
					break;
				}
				//获取modelId长度
				final int modelIdLength = (int)block[offset + idLength];
				//获取paragraphId
				final String paragraphId = new String(block, offset, idLength);
				offset += idLength + 1;
				//获取modelId
				final String modelId = (modelIdLength > 0) ? new String(block, offset, modelIdLength) : null;
				offset += modelIdLength;
				//获取文件中的paragraphNumber，去和paragraphIndex对比
				final int paragraphNumber = (int)block[offset] + (((int)block[offset + 1]) << 16);
				if(paragraphNumber < paragraphIndex || !paragraphId.contains("#")){
					offset += 2;
					last = paragraphId;
					continue;
				}
				if(last != null)
					return last.split("#")[last.split("#").length-1];
			}
		}
		return null;
	}
	/**
	 * author:DarkFlameMaster </br>
	 * time:2018/5/21 11:21 </br>
	 * desc:通过paragraphIndex获取段落Id的方法，遍历了.nlinks文件 </br>
	 */
	public String getParagraphId(int paragraphIndex){
//        Log.e("AbsoluteParagraphId1", "paragraphIndex: " + paragraphIndex);
		final int size = myInternalHyperlinks.size();
		for (int i = 0; i < size; ++i) {
			final char[] block = myInternalHyperlinks.block(i);
			for (int offset = 0; offset < block.length; ) {
				//获取id长度
				final int idLength = (int)block[offset++];
				if (idLength == 0) {
					break;
				}
				//获取modelId长度
				final int modelIdLength = (int)block[offset + idLength];
				//获取paragraphId
				final String paragraphId = new String(block, offset, idLength);
//				Log.e("AbsoluteParagraphId1", "paragraphId: " + paragraphId);
				offset += idLength + 1;
				//获取modelId
				final String modelId = (modelIdLength > 0) ? new String(block, offset, modelIdLength) : null;
				offset += modelIdLength;
				//获取文件中的paragraphNumber，去和paragraphIndex对比
				final int paragraphNumber = (int)block[offset] + (((int)block[offset + 1]) << 16);
//				Log.e("AbsoluteParagraphId1", "paragraphNumber: " + paragraphNumber + "|| block[offset] :" + ((int)block[offset]));
				if(paragraphNumber != paragraphIndex || !paragraphId.contains("#")){
					offset += 2;
					continue;
				}
//				Log.e("AbsoluteParagraphId1", "paragraphIndex: " + paragraphIndex);
//				Log.e("AbsoluteParagraphId1", "paragraphId: " + paragraphId);
//				Log.e("AbsoluteParagraphId1", "paragraphNumber: " + paragraphNumber);
				return paragraphId.split("#")[paragraphId.split("#").length-1];
			}
		}
//		return getParagraphIdByFixTrTag(paragraphIndex);
		return getParagraphId(paragraphIndex+1);
	}

	/**
	 * author:DarkFlameMaster </br>
	 * time:2018/5/22 9:48 </br>
	 * desc:通过段落id从.nlinks文件中反解析paragraphIndex </br>
	 */
	public int getparagraphIndex(String id) {
		final int len = id.length();
		final int size = myInternalHyperlinks.size();

		for (int i = 0; i < size; ++i) {
			final char[] block = myInternalHyperlinks.block(i);
			for (int offset = 0; offset < block.length; ) {
				final int idLength = (int)block[offset++];
				if (idLength == 0) {
					break;
				}
				final int modelIdLength = (int)block[offset + idLength];
				//获取paragraphId
				final String paragraphId = new String(block, offset, idLength);
				if (idLength < len || !paragraphId.contains(id)) {
					offset += idLength + modelIdLength + 3;
					continue;
				}
				offset += idLength + 1;
				final String modelId = (modelIdLength > 0) ? new String(block, offset, modelIdLength) : null;
				offset += modelIdLength;
				final int paragraphNumber = (int)block[offset] + (((int)block[offset + 1]) << 16);
				Logger.e(".nlinks本地段落id:%d",paragraphNumber);
				return paragraphNumber;
			}
		}
		return 0;
	}
}
