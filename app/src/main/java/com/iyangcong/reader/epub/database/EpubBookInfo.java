package com.iyangcong.reader.epub.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Date;

/**
 * Created by WuZepeng on 2018-01-02.
 */
@Entity(nameInDb = "epub_info")
public class EpubBookInfo {
	/**
	 * 本地数据库中的主键
	 */
	@Id(autoincrement = true)
	private long Id;

	/**
	 *Epub在后台的id;
	 */
	@Property(nameInDb = "bookId")
	private long bookId;

	/**
	 *Epub的状态，
	 * 目前有7种
	 * -1：异常状态，
	 * 0：初始状态，
	 * 1：下载完成
	 * 2：DES解密完成
	 * 3：DES解密失败
	 * 4：AES解密完成（DES解密完成）
	 * 5：AES解密失败（DES解密完成）
	 */
	@NotNull
	@Property(nameInDb = "state")
	private int epubState;

//	/**
//	 * des解密的密钥
//	 */
//	@Property(nameInDb = "des_key")
//	private String DESKey;
//
//	/**
//	 * aes加解密的密钥
//	 */
//	@Property(nameInDb = "aes_key")
//	private String AESKey;

	/**
	 * 时间戳
	 */
	@NotNull
	@Property(nameInDb = "timeStamp")
	private Date timeStamp;

	/**
	 * Epub语言版本。
	 */
	@NotNull
	@Property(nameInDb = "language")
	private int language;

	@Generated(hash = 880163342)
	public EpubBookInfo(long Id, long bookId, int epubState,
			@NotNull Date timeStamp, int language) {
		this.Id = Id;
		this.bookId = bookId;
		this.epubState = epubState;
		this.timeStamp = timeStamp;
		this.language = language;
	}

	@Generated(hash = 1869230094)
	public EpubBookInfo() {
	}

	public long getId() {
		return this.Id;
	}

	public void setId(long Id) {
		this.Id = Id;
	}

	public long getBookId() {
		return this.bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public int getEpubState() {
		return this.epubState;
	}

	public void setEpubState(int epubState) {
		this.epubState = epubState;
	}

	public Date getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getLanguage() {
		return this.language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "EpubBookInfo{" +
				"Id=" + Id +
				", bookId=" + bookId +
				", epubState=" + epubState +
				", timeStamp=" + timeStamp +
				", language=" + language +
				'}';
	}
}
