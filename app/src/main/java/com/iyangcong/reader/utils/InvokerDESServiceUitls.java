package com.iyangcong.reader.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.iyangcong.reader.EpubLibrary;
import com.iyangcong.reader.interfaceset.DESEncodeInvoker;
import com.orhanobut.logger.Logger;

import org.geometerplus.fbreader.formats.oeb.function.EpubService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuZepeng on 2017-12-21.
 * 调用DES解密服务的工具类
 */

public class InvokerDESServiceUitls implements DESEncodeInvoker,ServiceConnection{
	protected static EpubLibrary mEpubLibrary;
	private Context mContext;
	public static final int StartService = 0;
	public InvokerDESServiceUitls(Context context) {
		mContext = context;
	}

	@Override
	public void invokerDESEncodeService(long bookId) {
		try {
//			Logger.e("wzp mEpubLibrary == null:"+(mEpubLibrary == null));
			if(mEpubLibrary == null){
				Intent intent = new Intent(mContext,EpubService.class);
				mContext.bindService(intent,this, Context.BIND_AUTO_CREATE);
			}
			if(mEpubLibrary != null && bookId != 0)
				try {
					mEpubLibrary.startEpubDecodeAndEncode(bookId);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public void unbindService(){
		if(mEpubLibrary != null){
			mContext.unbindService(this);
			mEpubLibrary = null;
		}
	}

	@Override
	public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
		if(mEpubLibrary == null)
			mEpubLibrary = EpubLibrary.Stub.asInterface(iBinder);
	}

	@Override
	public void onServiceDisconnected(ComponentName componentName) {
		mEpubLibrary = null;
	}
}
