
package org.geometerplus.fbreader.formats.oeb.function.bean;

import java.io.Serializable;

public class Book implements Serializable {

    private long bookId=-1;

    private String title;

    private String englishTitle;

    private String author;

    private String bookAbstract;

    private String englishAbstract;
    
    public int type;

    private String priceRMB;
    private String bookState;
    private String priceUSD;
    private long compeleteSize;
    private long totalSize;
    private String bookThumbnailUrl;

    private String publisher;
    private String readableVersion;
    private String translator;
    /** 
    * @Fields discountType : TODO 0:非特价 1:限时特价  2: 不限时特价
    */ 
    private int discountType;
    private String discountPriceRMB;
    private String discountPriceUSD;
    private float enBookReadProgress;
    private float zhBookReadProgress;
    public String filePath;
    public int bookType;
    private int curReadType;
    private int status;
    
    //book的状态，如果4代表已经下载完成，可以阅读
    private String readOrDown;
    
    

    public String getReadOrDown() {
		return readOrDown;
	}

	public void setReadOrDown(String readOrDown) {
		this.readOrDown = readOrDown;
	}

	public void setBookState(String bookState) {
        this.bookState = bookState;
    }

    public String getBookState() {
        return bookState;
    }
    
    public long getCompeleteSize() {
        return compeleteSize;
    }
    public void setCompeleteSize(long compeleteSize) {
        this.compeleteSize = compeleteSize;
    }
    
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    public long getTotalSize() {
        return totalSize;
    }
    
    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEnglishTitle() {
        return englishTitle;
    }

    public void setEnglishTitle(String englishTitle) {
        this.englishTitle = englishTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookAbstract() {
        return bookAbstract;
    }

    public void setBookAbstract(String bookAbstract) {
        this.bookAbstract = bookAbstract;
    }

    public String getEnglishAbstract() {
        return englishAbstract;
    }

    public void setEnglishAbstract(String englishAbstract) {
        this.englishAbstract = englishAbstract;
    }


    public String getPriceRMB() {
        return priceRMB;
    }

    public void setPriceRMB(String priceRMB) {
        this.priceRMB = priceRMB;
    }

    public String getPriceUSD() {
        return priceUSD;
    }

    public void setPriceUSD(String priceUSD) {
        this.priceUSD = priceUSD;
    }

    public String getBookThumbnailUrl() {
        return bookThumbnailUrl;
    }

    public void setBookThumbnailUrl(String bookThumbnailUrl) {
        this.bookThumbnailUrl = bookThumbnailUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReadableVersion() {
        return readableVersion;
    }

    public void setReadableVersion(String readableVersion) {
        this.readableVersion = readableVersion;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public String getDiscountPriceRMB() {
        return discountPriceRMB;
    }

    public void setDiscountPriceRMB(String discountPriceRMB) {
        this.discountPriceRMB = discountPriceRMB;
    }

    public String getDiscountPriceUSD() {
        return discountPriceUSD;
    }

    public void setDiscountPriceUSD(String discountPriceUSD) {
        this.discountPriceUSD = discountPriceUSD;
    }

	public float getEnBookReadProgress() {
		return enBookReadProgress;
	}

	public void setEnBookReadProgress(float enBookReadProgress) {
		this.enBookReadProgress = enBookReadProgress;
	}

	public float getZhBookReadProgress() {
		return zhBookReadProgress;
	}

	public void setZhBookReadProgress(float zhBookReadProgress) {
		this.zhBookReadProgress = zhBookReadProgress;
	}

    public int getCurReadType() {
        return curReadType;
    }

    public void setCurReadType(int curReadType) {
        this.curReadType = curReadType;
    }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

    
}
