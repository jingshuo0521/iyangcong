package com.iyangcong.reader.wxapi;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * @ClassName: ${type_name}
 * @Description: 蒋统一下单之后组成的Object实体类，转为xml之后，发送到下单的URL连接
 * @author: ${libiao}
 * @date: ${date} ${time}
 * ${tags}
 */
public class HttpUtil {

    public static String sendPost(String url, String xmlObj) {

        //post请求返回结果
        HttpClient httpClient = new DefaultHttpClient();
        //httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");//解决中文乱码
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        // 使用POST方式提交数据
        try {
            //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
            StringEntity postEntity = new StringEntity(xmlObj, "UTF-8");
            postEntity.setContentEncoding("UTF-8");
            postEntity.setContentType("text/xml;charset=utf-8");
            httpPost.setEntity(postEntity);
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                String strResult = EntityUtils.toString(response.getEntity(), "UTF-8");
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            } else {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
            }


        } catch (Exception e) {
        } finally {
            httpPost.abort();
        }
        return result;
    }
}
