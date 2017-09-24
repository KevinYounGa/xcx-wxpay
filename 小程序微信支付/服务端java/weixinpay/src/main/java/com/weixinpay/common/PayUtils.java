

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.weixinpay.common.MD5;
import org.jeecgframework.p3.core.util.MD5Util;

@SuppressWarnings("deprecation")
public class PayUtils {
    private static Object Server;
    @SuppressWarnings("deprecation")
    public static DefaultHttpClient httpclient;
    private static SortedMap parameters;

    static {
        httpclient = new DefaultHttpClient();
       // httpclient = (DefaultHttpClient) HttpClientConnectionManager.getSSLInstance(httpclient);
        parameters = new TreeMap();
    }

    public static final String KEY_PATH = "E:/wxzf/cert/apiclient_cert.p12";

    /**
     * 把对象转换成字符串
     *
     * @param obj
     * @return String 转换成字符串,若对象为null,则返回空字符串.
     */
    public static String toString(Object obj) {
        if (obj == null)
            return "";

        return obj.toString();
    }

    /**
     * 把对象转换为int数值.
     *
     * @param obj
     *            包含数字的对象.
     * @return int 转换后的数值,对不能转换的对象返回0。
     */
    public static int toInt(Object obj) {
        int a = 0;
        try {
            if (obj != null) {
                a = Integer.parseInt(obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * 获取从1970年开始到现在的秒数
     *
     * @param date
     * @return
     */
    public static String getTimeStamp() {
        long seconds = System.currentTimeMillis() / 1000;
        return String.valueOf(seconds);
    }

    /**
     * 获取当前时间 yyyyMMddHHmmss
     * @return String
     */
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }

    /**
     * 获取当前日期 yyyyMMdd
     * @param date
     * @return String
     */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String strDate = formatter.format(date);
        return strDate;
    }

    /**
     * 取出一个指定长度大小的随机正整数.
     * @param length  int 设定所取出随机数的长度。length小于11
     * @return int 返回生成的随机数。
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

    /**
     * 获取编码字符集
     * @param request
     * @param response
     * @return String
     */

    public static String getCharacterEncoding(HttpServletRequest request, HttpServletResponse response) {

        if (null == request || null == response) {
            return "utf-8";
        }
        String enc = request.getCharacterEncoding();
        if (null == enc || "".equals(enc)) {
            enc = response.getCharacterEncoding();
        }
        if (null == enc || "".equals(enc)) {
            enc = "utf-8";
        }
        return enc;
    }

    public static String URLencode(String content) {
        String URLencode;
        URLencode = replace(Server.equals(content), "+", "%20");
        return URLencode;
    }

    private static String replace(boolean equals, String string, String string2) {
        return null;
    }

    /**
     * 获取unix时间，从1970-01-01 00:00:00开始的秒数
     * @param date
     * @return long
     */
    public static long getUnixTime(Date date) {
        if (null == date) {
            return 0;
        }
        return date.getTime() / 1000;
    }

    public static String QRfromGoogle(String chl) {
        int widhtHeight = 300;
        String EC_level = "L";
        int margin = 0;
        String QRfromGoogle;
        chl = URLencode(chl);
        QRfromGoogle = "http://chart.apis.google.com/chart?chs=" + widhtHeight + "x" + widhtHeight + "&cht=qr&chld="
                + EC_level + "|" + margin + "&chl=" + chl;
        return QRfromGoogle;
    }

    /**
     * 时间转换成字符串
     * @param date  时间
     * @param formatType  格式化类型
     * @return String
     */
    public static String date2String(Date date, String formatType) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        return sdf.format(date);
    }



    /**
     * 创建签名SHA1
     * @param signParams
     * @return
     * @throws Exception
     */
    public static String createSHA1Sign(SortedMap<String, String> signParams) throws Exception {
        StringBuffer sb = new StringBuffer();
        Set es = signParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k + "=" + v + "&");
            // 要采用URLENCODER的原始值！
        }
        String params = sb.substring(0, sb.lastIndexOf("&"));
        return getSha1(params);
    }

    /**
     * Sha1签名
     * @param str
     * @return
     */
    public static String getSha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得预支付订单号
     * @param url
     * @param xmlParam
     * @return
     */
    public static String getPayNo(String url, String xmlParam) {
        String prepay_id = "";
        try {
            String jsonStr = postWithXmlParams(url, xmlParam);
            if (jsonStr.indexOf("FAIL") != -1) {
                return prepay_id;
            }
            Map<String, Object> map = doXMLParse(jsonStr);
            prepay_id = (String) map.get("prepay_id");
            System.out.println("prepay_id:" + prepay_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prepay_id;
    }

    /**
     * 发送请求
     * @param url 请求路径
     * @param xmlParams xml字符串
     * @return
     */
    public static String postWithXmlParams(String url, String xmlParams) {
        HttpPost httpost = new HttpPost(url);
        try {
            httpost.setEntity(new StringEntity(xmlParams, "UTF-8"));
            HttpResponse response = httpclient.execute(httpost);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Map doXMLParse(String strxml) throws Exception {
        if (null == strxml || "".equals(strxml)) {
            return null;
        }
        Map m = new HashMap();
        InputStream in = String2Inputstream(strxml);
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }
            m.put(k, v);
        }
        // 关闭流
        in.close();
        return m;
    }

    /**
     * 获取子结点的xml
     * @param children
     * @return String
     */
    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }
        return sb.toString();
    }

    public static InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    public String getParameter(String parameter) {
        String s = (String) this.parameters.get(parameter);
        return (null == s) ? "" : s;
    }

    /**
     * 特殊字符处理
     * @param src
     * @return
     * @throws UnsupportedEncodingException
     */
    public String UrlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, "UTF-8").replace("+", "%20");
    }

    /**
     * 获取package的签名包
     * @param packageParams
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    public String genPackage(SortedMap<String, String> packageParams, String key) throws UnsupportedEncodingException {
        String sign = createSign(packageParams, key);

        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k + "=" + UrlEncode(v) + "&");
        }

        // 去掉最后一个&
        String packageValue = sb.append("sign=" + sign).toString();
        return packageValue;
    }

    /**
     * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
     */
    public static String createSign(SortedMap<String, String> packageParams, String key) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + key);
        System.out.println("md5:" + sb.toString());
        String sign =  MD5.MD5Encode(sb.toString()).toUpperCase();
        System.out.println("packge签名:" + sign);
        return sign;

    }

    /**
     * 创建package签名
     */
    public boolean createMd5Sign(String signParams) {
        StringBuffer sb = new StringBuffer();
        Set es = this.parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }

        // 算出摘要
        String sign = MD5.MD5Encode(sb.toString()).toUpperCase();
        String paySign = this.getParameter("sign").toLowerCase();
        return paySign.equals(sign);
    }

    /**
     * 输出XML
     * @return
     */
    public String parseXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = this.parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"appkey".equals(k)) {

                sb.append("<" + k + ">" + getParameter(k) + "</" + k + ">\n");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public static String post(String url, String xmlParam) {
        StringBuilder sb = new StringBuilder();
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File(KEY_PATH));
            String mchid = ResourceUtil.getConfigByName("wx.application.mch_id");
            try {
                keyStore.load(instream, mchid.toCharArray());
            } finally {
                instream.close();
            }

            // 证书
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, mchid.toCharArray())
                    .build();
            // 只允许TLSv1协议
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            //创建基于证书的httpClient,后面要用到
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();

            HttpPost httpPost = new HttpPost(url);//退款接口
            StringEntity reqEntity = new StringEntity(xmlParam);
            // 设置类型
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);
            CloseableHttpResponse response = client.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    String text = "";
                    while ((text = bufferedReader.readLine()) != null) {
                        sb.append(text);
                    }
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /*try {

                HttpGet httpget = new HttpGet("https://api.mch.weixin.qq.com/secapi/pay/refund");

                System.out.println("executing request" + httpget.getRequestLine());

                CloseableHttpResponse response = httpclient.execute(httpget);
                try {
                    HttpEntity entity = response.getEntity();

                    System.out.println("----------------------------------------");
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        System.out.println("Response content length: " + entity.getContentLength());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                        String text;
                        while ((text = bufferedReader.readLine()) != null) {
                            System.out.println(text);
                            sb.append(text);
                        }

                    }
                    EntityUtils.consume(entity);
                } finally {
                    response.close();
                }
            } finally {
                httpclient.close();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
