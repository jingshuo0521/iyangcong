package com.iyangcong.reader.bean;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;



@DatabaseTable(tableName = "readingDate")
public class ReadingDataBean implements Comparable<ReadingDataBean>{
	@DatabaseField(generatedId = true,columnName = "id")
	long id;
	@DatabaseField(columnName = "userId",dataType = DataType.LONG)
	@SerializedName("userId")
	private long userId;
	@DatabaseField(columnName = "bookId",dataType = DataType.LONG)
	@SerializedName("bookId")
	private long bookId;
	@DatabaseField(columnName = "languageType",dataType = DataType.INTEGER)
	@SerializedName("language")
	private int languageType;
	@DatabaseField(columnName = "startParagraphId",dataType = DataType.INTEGER)
	@SerializedName("sParagrah")
	private int startParagraphId;
	@DatabaseField(columnName = "endParagraphId",dataType = DataType.INTEGER)
	@SerializedName("eParagraph")
	private int endParagraphId;
	@DatabaseField(columnName = "startTime",dataType = DataType.STRING)
	@SerializedName("sTime")
	private String startTime;
	@DatabaseField(columnName = "endTime",dataType = DataType.STRING)
	@SerializedName("endTime")
	private String endTime;
	@DatabaseField(columnName = "uuid",dataType = DataType.STRING)
	@SerializedName("uuid")
	private String uuid;

	@Override
	public int compareTo(@NonNull ReadingDataBean readingDataBean) {
		return readingDataBean==null?-1:readingDataBean.getUuid().equals(uuid)?0:1;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public int getLanguageType() {
		return languageType;
	}

	public void setLanguageType(int languageType) {
		this.languageType = languageType;
	}

	public int getStartParagraphId() {
		return startParagraphId;
	}

	public void setStartParagraphId(int startParagraphId) {
		this.startParagraphId = startParagraphId;
	}

	public int getEndParagraphId() {
		return endParagraphId;
	}

	public void setEndParagraphId(int endParagraphId) {
		this.endParagraphId = endParagraphId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
