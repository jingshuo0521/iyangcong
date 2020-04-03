package org.geometerplus.fbreader.formats.oeb.function.encryp;

/**
 * Created by WuZepeng on 2017-12-12.
 */

public interface CacheHelper<A,B>{

	public A isCached(B b);

	public boolean cache(A a,B b);
}
