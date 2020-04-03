package com.iyangcong.reader.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.iyangcong.reader.bean.WeChatPayResult;
import com.iyangcong.reader.event.SlideEvent;
import com.iyangcong.reader.utils.Constants;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		Logger.i("onPayFinish transaction :"+ req.transaction);
	}

	@Override
	public void onResp(BaseResp resp) {
		Logger.d("onPayFinish, errCode = " + resp.errCode+"  transaction:" + resp.transaction + " openId"+ resp.openId + " errStr:" + resp.errStr);
//		Intent intent = new Intent();
//		intent.setAction("com.iyangcong.errCode");
//		intent.putExtra("errCode",resp.errCode);
//		sendBroadcast(intent);
		EventBus.getDefault().post(new WeChatPayResult(resp.errCode,resp.errStr));
		finish();
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(R.string.app_tip);
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
//		}
	}
}