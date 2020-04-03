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

package org.geometerplus.android.fbreader;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import com.iyangcong.reader.activity.VideoPlayerActivity;
import com.iyangcong.reader.utils.Constants;

import org.geometerplus.android.fbreader.httpd.DataUtil;
import org.geometerplus.android.util.UIMessageUtil;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.util.MimeType;
import org.geometerplus.zlibrary.text.view.ZLTextVideoElement;
import org.geometerplus.zlibrary.text.view.ZLTextVideoRegionSoul;

import java.io.File;
import java.io.FileOutputStream;

class OpenVideoAction extends FBAndroidAction {
	OpenVideoAction(FBReader baseActivity, FBReaderApp fbreader) {
		super(baseActivity, fbreader);
	}

	public void getPicture(){
		//设置视频路径
		String path = Environment.getExternalStorageDirectory() + "/demo.mp4";
		//将路径实例化为一个文件对象
		File file = new File(path);
		//判断对象是否存在，不存在的话给出Toast提示
		if(file.exists()){
			//提供统一的接口用于从一个输入媒体中取得帧和元数据
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(path);
			String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

			int seconds = Integer.valueOf(time) / 1000;

			seconds = 10;

			for (int i = 1; i < seconds; i++){
				Bitmap bitmap = retriever.getFrameAtTime(i*1000*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
				String pathPic = Environment.getExternalStorageDirectory()+ File.separator + i + ".jpg";

				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(pathPic);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
					fos.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else {
		}
	}

	@Override
	protected void run(Object ... params) {
		if (params.length != 1 || !(params[0] instanceof ZLTextVideoRegionSoul)) {
			return;
		}

		final ZLTextVideoElement element = ((ZLTextVideoRegionSoul)params[0]).VideoElement;
		boolean playerNotFound = false;
		String url;
		for (MimeType mimeType : MimeType.TYPES_VIDEO) {
			final String mime = mimeType.toString();
			final String path = element.Sources.get(mime);
			if (path == null) {
				continue;
			}
			if (path.contains("http")){
				//这儿需要处理几种异常，包括补全URL
				if((!path.contains("http://"))&&(path.contains("http:/"))){
					//如果URL少一个斜线则补全
					url = path.replace("http:/","");
					url = "http://"+url;
				}else if((!path.contains("https://"))&&(path.contains("https:/"))){
					//如果URL少一个斜线则补全
					url = path.replace("https:/","");
					url = "http://"+url;
				}else {
					url = path;
				}
			}else {
				url = DataUtil.buildUrl(BaseActivity.DataConnection, mime, path);
			}
			//final Intent intent = new Intent(Intent.ACTION_VIEW);

			if (url == null) {
				UIMessageUtil.showErrorMessage(BaseActivity, "videoServiceNotWorking");
				return;
			}
			//intent.setDataAndType(Uri.parse(url), mime);
			try {
				Intent intent = new Intent(BaseActivity, VideoPlayerActivity.class);
				intent.putExtra(Constants.VIDEO_TITLE,"阅读器");
				intent.putExtra(Constants.VIDEO_ADDRESS, url);//url);
				intent.putExtra(Constants.VIDEO_COVER,"http://testback.iyangcong.com/onionback/upload/subject/photo/%E7%BD%91%E9%A1%B5201803210745370791451.jpg");
				BaseActivity.startActivity(intent);
				return;
			} catch (ActivityNotFoundException e) {
				playerNotFound = true;
				continue;
			}
		}
		if (playerNotFound) {
			UIMessageUtil.showErrorMessage(BaseActivity, "videoPlayerNotFound");
		}
	}
}
