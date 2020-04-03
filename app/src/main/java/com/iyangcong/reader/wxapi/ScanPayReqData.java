package com.iyangcong.reader.wxapi;


import com.alipay.share.sdk.Constant;
import com.iyangcong.reader.utils.Constants;

import org.apache.http.NameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @ClassName: ${type_name}
 * @Description: ${todo}
 * @author: ${libiao}
 * @date: ${date} ${time}
 * ${tags}
 */
public class ScanPayReqData {
    //每个字段具体的意思请查看API文档
    private String appid = "";//微信分配的公众账号ID（企业号corpid即为此appId）
    private String mch_id = "";//微信支付分配的商户号
    private String nonce_str = "";//随机字符串
    private String sign = "";//签名
    private String body = "";//商品描述
    private String out_trade_no = "";//商户订单号
    private int total_fee = 0;//总金额
    private String spbill_create_ip = "";//终端ip

    private String notify_url = "";//接收微信支付异步通知回调地址,不能携带任何参数
    private String trade_type = "";//交易类型JSAPI，NATIVE，APP
    private String product_id = "";//商品ID,trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。

    private String openid;//交易类型JSAPI,必填      用户在公众号下的唯一标识
    
    public ScanPayReqData(){}

    /**
     * 微信扫码支付模式二
     * @param body
     * @param total_fee
     * @param product_id
     * @param out_trade_no
     * @param spbill_create_ip
     */
    public ScanPayReqData(String body, int total_fee,
						  String product_id, String out_trade_no, String spbill_create_ip) {

        setAppid(Constants.WECHAT_APP_ID);
        setMch_id(Constants.WECHAT_MCH_ID);
        setNonce_str(WeiXinUtil.getRandomStringByLength(32));
        setBody(body);
        setOut_trade_no(out_trade_no);//商户订单号
        setTotal_fee(total_fee);
        setSpbill_create_ip(spbill_create_ip);
        setNotify_url(Constants.WXPAY_NOTIFY_URL);
        setTrade_type("APP");
        setProduct_id(product_id);

        //根据API给的签名规则进行签名
        String sign = Signature.getSign(toMap());
        setSign(sign);//把签名数据设置到Sign这个属性中
    }



    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if(obj!=null){
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    
    

 // 采用排序的Dictionary的好处是方便对数据包进行签名，不用再签名之前再做一次排序
 	private TreeMap<String, Object> m_values = new TreeMap<String, Object>();
 	
 	
 	
 	
 	
	/**
	 * @throws UnsupportedEncodingException
	 * @Dictionary格式转化成url参数格式 @ return url格式串, 该串不包含sign字段值
	 */
	public String getValue(String key) throws Exception, UnsupportedEncodingException {
		String result = "";
		for (Entry<String, Object> pair : m_values.entrySet()) {
			if (pair.getValue() == null) {
				
				throw new Exception("WxPayData内部含有值为null的字段!");
			}

			if (pair.getKey().equalsIgnoreCase(key)
					&& !pair.getValue().toString().equalsIgnoreCase("")) {
				result= pair.getValue().toString();
			}
		}
		
		return result;
	}

 	/**
 	 * 设置某个字段的值
 	 * 
 	 * @param key
 	 *            字段名
 	 * @param value
 	 *            字段值
 	 */
 	public void SetValue(String key, Object value) {
 		m_values.put(key, value);
 	}

 	/**
 	 * 根据字段名获取某个字段的值
 	 * 
 	 * @param key
 	 *            字段名
 	 * @return key对应的字段值
 	 */
 	public Object GetValue(String key) {
 		return m_values.get(key);
 	}

 	/**
 	 * 判断某个字段是否已设置
 	 * 
 	 * @param key
 	 *            字段名
 	 * @return 若字段key已被设置，则返回true，否则返回false
 	 */
 	public boolean IsSet(String key) {
 		return m_values.containsKey(key);
 	}

 	/**
 	 * @将Dictionary转成xml
 	 * @return 经转换得到的xml串
 	 **/
 	public String ToXml() throws Exception {
 		// 数据为空时不能转化为xml格式
 		if (0 == m_values.size()) {
 			
 			throw new Exception("WxPayData数据为空!");
 		}

 		String xml = "<xml>";
 		for (Entry<String, Object> pair : m_values.entrySet()) {
 			// 字段值不能为null，会影响后续流程
 			if (pair.getValue() == null) {
 				//Log.error("WxPayData内部含有值为null的字段!");
 				throw new Exception("WxPayData内部含有值为null的字段!");
 			}

 			if (pair.getValue() instanceof Integer) {
 				xml += "<" + pair.getKey() + ">" + pair.getValue() + "</"
 						+ pair.getKey() + ">";
 			} else if (pair.getValue() instanceof String) {
 				xml += "<" + pair.getKey() + ">" + "<![CDATA["
 						+ pair.getValue() + "]]></" + pair.getKey() + ">";
 			} else// 除了string和int类型不能含有其他数据类型
 			{
 				
 				throw new Exception("WxPayData字段数据类型错误!");
 			}
 		}
 		xml += "</xml>";
 		return xml;
 	}

 	/**
 	 * @将xml转为WxPayData对象并返回对象内部的数据
 	 *            待转换的xml串
 	 * @return 经转换得到的Dictionary
 	 * @throws ParserConfigurationException
 	 * @throws IOException
 	 * @throws SAXException
 	 */
 	public TreeMap<String, Object> FromXml(String xml) throws Exception,
			ParserConfigurationException, SAXException, IOException {
 		if (xml == null || xml.isEmpty()) {
 			//Log.error("将空的xml串转换为WxPayData不合法!");
 			throw new Exception("将空的xml串转换为WxPayData不合法!");
 		}

 		DocumentBuilderFactory documentBuildFactory = DocumentBuilderFactory
 				.newInstance();
 		DocumentBuilder doccumentBuilder = documentBuildFactory
 				.newDocumentBuilder();
 		Document document = doccumentBuilder.parse(new ByteArrayInputStream(xml
 				.getBytes("UTF-8")));
 		Node xmlNode = document.getFirstChild();// 获取到根节点<xml>
 		NodeList nodes = xmlNode.getChildNodes();
 		for (int i = 0, length = nodes.getLength(); i < length; i++) {
 			Node xn = nodes.item(i);
 			if (xn instanceof Element) {
 				Element xe = (Element) xn;
 				m_values.put(xe.getNodeName(), xe.getTextContent());// 获取xml的键值对到WxPayData内部的数据中
 			}
 		}

 		try {
 			// 2015-06-29 错误是没有签名
 			if (m_values.get("return_code") == null
 					|| !m_values.get("return_code").toString()
 							.equalsIgnoreCase("SUCCESS")) {
 				return m_values;
 			}
 			//CheckSign();// 验证签名,不通过会抛异常
 		} catch (Exception ex) {
 			throw new Exception(ex.getMessage());
 		}

 		return m_values;
 	}

 	/**
 	 * @throws UnsupportedEncodingException
 	 * @Dictionary格式转化成url参数格式 @ return url格式串, 该串不包含sign字段值
 	 */
 	public String ToUrl() throws Exception, UnsupportedEncodingException {
 		String buff = "";
 		for (Entry<String, Object> pair : m_values.entrySet()) {
 			if (pair.getValue() == null) {
 				//Log.error("WxPayData内部含有值为null的字段!");
 				throw new Exception("WxPayData内部含有值为null的字段!");
 			}

 			if (!pair.getKey().equalsIgnoreCase("sign")
 					&& !pair.getValue().toString().equalsIgnoreCase("")) {
 				buff += pair.getKey() + "=" + pair.getValue().toString() + "&";
 			}
 		}
 		String regpattern = "&+$";
 		Pattern pattern = Pattern.compile(regpattern, Pattern.CASE_INSENSITIVE);
 		Matcher m = pattern.matcher(buff);
 		if (m.find()) {
 			buff = buff.substring(0, buff.length() - 1);
 		}
 		return buff;
 	}

 

 	/**
 	 * @values格式化成能在Web页面上显示的结果（因为web页面上不能直接输出xml格式的字符串）
 	 */
 	public String ToPrintStr() throws Exception {
 		String str = "";
 		for (Entry<String, Object> pair : m_values.entrySet()) {
 			if (pair.getValue() == null) {
 				//Log.error("WxPayData内部含有值为null的字段!");
 				throw new Exception("WxPayData内部含有值为null的字段!");
 			}

 			str += pair.getKey() + "=" + pair.getValue().toString() + "<br>";
 		}

 		return str;
 	}

 	/**
 	 * @生成签名，详见签名生成算法
 	 * @return 签名, sign字段不参加签名
 	 * @throws NoSuchAlgorithmException
 	 * @throws UnsupportedEncodingException
 	 */
 	public String MakeSign(String type) throws Exception, NoSuchAlgorithmException,
			UnsupportedEncodingException {
 		// 转url格式
 		String str = ToUrl();
 		// 在string后加入API KEY
 		str += "&key="+Constants.WX_PAY_KEY;
 		// MD5加密
 		MessageDigest md = MessageDigest.getInstance("MD5");
 		byte[] array = md.digest(str.getBytes("UTF-8"));
 		StringBuffer sb = new StringBuffer();
 		for (int i = 0; i < array.length; i++) {
 			String hex = Integer.toHexString(0xFF & array[i]);
 			if (hex.length() == 1) {
 				sb.append('0');
 			}
 			sb.append(hex);
 		}
 		// 所有字符转为大写
 		return sb.toString().toUpperCase();
 	}

	/**
	 * 在app端生成签名
	 *
	 * @param params
	 * @return
	 */
	public String genAppSign(List<NameValuePair> params) {
		StringBuilder str = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			str.append(params.get(i).getName());
			str.append('=');
			str.append(params.get(i).getValue());
			str.append('&');
		}
		str.append("key=");
		str.append(Constants.WX_PAY_KEY);

		String appSign = MD5.getMessageDigest(str.toString().getBytes())
				.toUpperCase();
		return appSign;
	}


 	/**
 	 * 
 	 * 检测签名是否正确 正确返回true，错误抛异常
 	 * 
 	 * @throws UnsupportedEncodingException
 	 * @throws NoSuchAlgorithmException
 	 */
 	public boolean CheckSign() throws Exception, NoSuchAlgorithmException,
			UnsupportedEncodingException {
 		// 如果没有设置签名，则跳过检测
 		if (!IsSet("sign")) {
 			//Log.error("WxPayData签名存在但不合法!");
 			throw new Exception("WxPayData签名存在但不合法!");
 		}
 		// 如果设置了签名但是签名为空，则抛异常
 		else if (GetValue("sign") == null
 				|| GetValue("sign").toString().equalsIgnoreCase("")) {
 			//Log.error("WxPayData签名存在但不合法!");
 			throw new Exception("WxPayData签名存在但不合法!");
 		}

 		// 获取接收到的签名
 		String return_sign = GetValue("sign").toString();

 		// 在本地计算新的签名
 		String cal_sign = MakeSign("MD5");

 		if (cal_sign.equalsIgnoreCase(return_sign)) {
 			return true;
 		}

 		//Log.error("WxPayData签名验证错误!");
 		throw new Exception("WxPayData签名验证错误!");
 	}

 	/**
 	 * @获取Dictionary
 	 */
 	public TreeMap<String, Object> GetValues() {
 		return m_values;
 	}
 	
 	 // 生成当前时间
    public long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }
}
