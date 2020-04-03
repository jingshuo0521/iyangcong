package org.geometerplus.android.fbreader;

import org.geometerplus.fbreader.fbreader.FBReaderApp;

/**
 * Created by ljw on 2017/4/22.
 */

public class ChangeLanguageAction extends FBAndroidAction {

    private volatile FBReader baseActivity;

    ChangeLanguageAction(FBReader baseActivity, FBReaderApp fbreader) {
        super(baseActivity, fbreader);
        this.baseActivity = baseActivity;
    }

    @Override
    protected void run(Object... params) {
        if (baseActivity != null) {
            baseActivity.changeLanguage(true);
        }
    }
}
