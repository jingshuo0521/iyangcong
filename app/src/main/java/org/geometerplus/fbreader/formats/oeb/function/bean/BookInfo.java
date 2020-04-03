
package org.geometerplus.fbreader.formats.oeb.function.bean;

import java.io.Serializable;
import java.util.List;

public class BookInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Book book;
    private List<BookChapter> enChaptersList;
    private List<BookChapter> zhChaptersList;
    
    
    private int minChapterId;
    
    
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    
    public int getMinChapterId() {
		return minChapterId;
	}

	public void setMinChapterId(int minChapterId) {
		this.minChapterId = minChapterId;
	}


	public List<BookChapter> getEnChaptersList() {
        return enChaptersList;
    }

    public void setEnChaptersList(List<BookChapter> enChaptersList) {
        this.enChaptersList = enChaptersList;
    }

    public List<BookChapter> getZhChaptersList() {
        return zhChaptersList;
    }

    public void setZhChaptersList(List<BookChapter> zhChaptersList) {
        this.zhChaptersList = zhChaptersList;
    }
}
