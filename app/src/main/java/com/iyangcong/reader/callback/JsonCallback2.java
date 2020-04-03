package com.iyangcong.reader.callback;

import android.content.Context;

import com.google.gson.stream.JsonReader;
import com.iyangcong.reader.interfaceset.DeviceType;
import com.iyangcong.reader.model.IycResponse;
import com.iyangcong.reader.model.SimpleResponse;
import com.iyangcong.reader.utils.Convert;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

public abstract class JsonCallback2<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;
    private Context context;
    public JsonCallback2(Type type) {
        this.type = type;
    }

    public JsonCallback2(Class<T> clazz) {
        this.clazz = clazz;
    }
    public JsonCallback2(Context context) {
        this.context = context;
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现
        request.params("deviceType", DeviceType.ANDROID_3);
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     * <pre>
     * OkGo.get(Urls.URL_METHOD)//
     *     .tag(this)//
     *     .execute(new DialogCallback<IycResponse<ServerModel>>(this) {
     *          @Override
     *          public void onSuccess(IycResponse<ServerModel> responseData, Call call, Response response) {
     *              handleResponse(responseData.data, call, response);
     *          }
     *     });
     * </pre>
     */
    @Override
    public T convertSuccess(Response response) throws Exception {

//        // 不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
//
//        //以下代码是通过泛型解析实际参数,泛型必须传
//        //这里为了方便理解，假如请求的代码按照上述注释文档中的请求来写，那么下面分别得到是
//
//        //com.lzy.demo.callback.DialogCallback<com.lzy.demo.model.IycResponse<com.lzy.demo.model.ServerModel>> 得到类的泛型，包括了泛型参数
//        JsonReader jsonReader = new JsonReader(response.body().charStream());
//        Type genType = getClass().getGenericSuperclass();
//        //从上述的类中取出真实的泛型参数，有些类可能有多个泛型，所以是数值
//        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
//        //我们的示例代码中，只有一个泛型，所以取出第一个，得到如下结果
//        //com.lzy.demo.model.IycResponse<com.lzy.demo.model.ServerModel>
//        Type type = params[0];
//
//        // 这里这么写的原因是，我们需要保证上面我解析到的type泛型，仍然还具有一层参数化的泛型，也就是两层泛型
//        // 如果你不喜欢这么写，不喜欢传递两层泛型，那么以下两行代码不用写，并且javabean按照
//        // https://github.com/jeasonlzy/okhttp-OkGo/blob/master/README_JSONCALLBACK.md 这里的第一种方式定义就可以实现
//        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
//        //如果确实还有泛型，那么我们需要取出真实的泛型，得到如下结果
//        //class com.lzy.demo.model.IycResponse
//        //此时，rawType的类型实际上是 class，但 Class 实现了 Type 接口，所以我们用 Type 接收没有问题
//        Type rawType = ((ParameterizedType) type).getRawType();
//
//        //这里我们既然都已经拿到了泛型的真实类型，即对应的 class ，那么当然可以开始解析数据了，我们采用 Gson 解析
//        //以下代码是根据泛型解析数据，返回对象，返回的对象自动以参数的形式传递到 onSuccess 中，可以直接使用
//
//        if (rawType == Void.class) {
//            //无数据类型,表示没有data数据的情况（以  new DialogCallback<IycResponse<Void>>(this)  以这种形式传递的泛型)
//            SimpleResponse simpleResponse = Convert.fromJson(jsonReader, SimpleResponse.class);
//            response.close();
//            //noinspection unchecked
//            return (T) simpleResponse.toIycResponse();
//        } else if (rawType == IycResponse.class) {
//            //有数据类型，表示有data
//            IycResponse IycResponse = Convert.fromJson(jsonReader, type);
//            response.close();
//            int code = IycResponse.statusCode;
//            //这里的0是以下意思
//            //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
//            if (code == 0) {
//                //noinspection unchecked
//                return (T) IycResponse;
//            } else if (code == 104) {
//                //比如：用户授权信息无效，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
//                throw new IllegalStateException("用户授权信息无效");
//            } else if (code == 105) {
//                //比如：用户收取信息已过期，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
//                throw new IllegalStateException("用户收取信息已过期");
//            } else if (code == 106) {
//                //比如：用户账户被禁用，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
//                throw new IllegalStateException("用户账户被禁用");
//            } else {
//                throw new IllegalStateException(IycResponse.msg);
//            }
//        } else {
//            response.close();
//            throw new IllegalStateException("基类错误无法解析!");
//        }

        //shao begin
        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
               // return convert.convertResponse(response);
                return convert.convertSuccess(response);
            }
        }

        JsonConvert<T> convert = new JsonConvert<>(type);
        //return convert.convertResponse(response);
        return convert.convertSuccess(response);
        //shao end
    }

    @Override
    public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);
//        try {
//            NormalDialog dialog = new NormalDialog(context);
//            DialogUtils.setAlertDialogNormalStyle(dialog, "提示", e.getMessage());
//        }catch (Exception error){
//            Logger.d(e.getMessage());
//        }
//        BroadCastSender.sendBroadCast(context);
    }
}