package org.geometerplus.android.fbreader;

import com.iyangcong.reader.utils.SharedPreferenceUtil;

import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.zlibrary.core.util.ZLColor;

/**
 * Created by ljw on 2017/4/7.
 */

public class UpdateBackGroundColorAction extends FBAndroidAction {

    UpdateBackGroundColorAction(FBReader baseActivity, FBReaderApp fbreader) {
        super(baseActivity, fbreader);
    }

    @Override
    protected void run(Object ... params) {
        Reader.ViewOptions.getColorProfile().BackgroundOption.setValue(new ZLColor(SharedPreferenceUtil.getInstance().getInt(SharedPreferenceUtil.CURRENT_BACKGROUND_COLOR,0)));
        Reader.getViewWidget().reset();
        Reader.getViewWidget().repaint();
    }
}
