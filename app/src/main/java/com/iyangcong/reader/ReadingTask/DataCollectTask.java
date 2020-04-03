package com.iyangcong.reader.ReadingTask;

public interface DataCollectTask<T> extends Cloneable{
	boolean checkExecutParams(T t);
	void execute(T t);
	boolean hasExecuted();
}
