package com.iyangcong.reader.callback;

import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.utils.CommonUtil;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.convert.FileConvert;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;

import okhttp3.Response;

/**
 * Created by ljw on 2017/3/15.
 */

public abstract class FileCallback extends AbsCallback<File> {

    private FileConvert convert;    //文件转换类

    public FileCallback() {
        this(null);
    }

    public FileCallback(String destFileName) {
        this(CommonUtil.getBooksDir(), destFileName);
    }

    public FileCallback(String destFileDir, String destFileName) {
        convert = new FileConvert(destFileDir, destFileName);
        convert.setCallback(this);
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        request.params("deviceType", DeviceType.ANDROID_3);
    }

    @Override
    public File convertSuccess(Response response) throws Exception {
        File file = convert.convertSuccess(response);
        response.close();
        return file;
    }
}
