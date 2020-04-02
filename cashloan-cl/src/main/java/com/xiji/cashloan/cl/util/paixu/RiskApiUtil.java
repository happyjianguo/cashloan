package com.xiji.cashloan.cl.util.paixu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiji.cashloan.cl.util.HttpUtils;
import com.xiji.cashloan.core.common.context.Global;
import okhttp3.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * 工具类
 *
 */
public class RiskApiUtil {
    //测试机构appId
    private static String appId = Global.getValue("px_model_app_id");
    //测试签名密钥
    private static String signKey = Global.getValue("px_model_sign");
    private static String secret = Global.getValue("px_model_secret");
    private static String URL = Global.getValue("px_model_url");

    ApiClientUtil apiClientUtil;

    private static final Logger log = LoggerFactory.getLogger(RiskApiUtil.class);

    private static ContentType CONTENT_TYPE = ContentType.create("application/json", "utf-8");

    public RiskApiUtil() {
        apiClientUtil = new ApiClientUtil();
    }


    /**
     * 模型3
     * @param type
     * @param name
     * @param idCard
     * @param phone
     * @param orderNo
     * @param dataObj
     * @return
     */
    public JSONObject mode3(String type, String name, String idCard, String phone,String orderNo, String dataObj) {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("name", name);
        json.put("idCard", idCard);
        json.put("phone", phone);
        json.put("dataObj", dataObj);
        json.put("orderNo",orderNo);
        JSONObject ret = apiClientUtil.submit2("mode3", json);
        log.info(String.format("【mode3风控结果】:mode2Result=%s",ret.toString()));
        return ret;
    }

    class ApiClientUtil {
//        private OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .build();

        /**
         * Http Post Json
         *
         * @param bizObj	业务对象
         * @return
         */
        public   JSONObject submit2(String call, Object bizObj) {
            String jsonStr = genJsonStr(call, JSONObject.parseObject(JSONObject.toJSONString(bizObj)), appId, signKey, secret);

            long start = System.currentTimeMillis();
            try {
                CloseableHttpClient httpclient = HttpUtils.createClient(URL);
                HttpPost postRequest = new HttpPost(URL);
                postRequest.setConfig(HttpUtils.getRequestConfig());
                if (!org.apache.commons.lang3.StringUtils.isBlank(jsonStr)) {
                    StringEntity entity = new StringEntity(jsonStr, CONTENT_TYPE);
                    postRequest.setEntity(entity);
                }
                HttpResponse httpResponse = httpclient.execute(postRequest);
                log.info(String.format("【调用第三方耗时】：use=%s", System.currentTimeMillis() - start));

                InputStream respIs = httpResponse.getEntity().getContent();
                String result = HttpUtils.convertStreamToString(respIs);
                log.info("【调用第三方】返回结果:" + result);
                JSONObject ret = JSONObject.parseObject(result);
                log.info("secret=" + secret);
                String de = aesDecrypt(ret.getString("body"), secret);
                log.info("body解密后数据:" + de);
                JSONObject body = JSONObject.parseObject(aesDecrypt(ret.getString("body"), secret));
                ret.put("body", body);
                return ret;
            } catch (Exception e) {
                log.error(String.format("【调用第三方异常】：use=%s, %s", System.currentTimeMillis() - start, e.getMessage()),e);
            }
            return null;
        }

        /**
         * 生成请求json
         *
         * @param call		接口名称
         * @param biz		业务字段Json
         * @param appId		appId
         * @param signKey	签名密钥
         * @return
         */
        public  String genJsonStr(String call, JSON biz, String appId, String signKey, String secret) {

            TreeMap<String, Object> sortedParams = new TreeMap<String, Object>();
            sortedParams.put("appId", appId);
            sortedParams.put("signKey", signKey);
            //接口名称
            sortedParams.put("call", call);
            sortedParams.put("biz", biz.toJSONString());
            sortedParams.put("timestamp", System.currentTimeMillis()+"");
            sortedParams.put("signType", "MD5");
            sortedParams.put("version", "1");
            //生成签名
            String sign = genSign(sortedParams);

            //请求json
            JSONObject json = new JSONObject();
            JSONObject info = new JSONObject(sortedParams);
            //info加密字段
            json.put("info", aesEncrypt(info.toString(), secret));
            //签名字段
            json.put("sign", sign);
            return json.toString();
        }

        /**
         * 生成签名
         *
         * MD5(自然升序(key=value&key=value&..))
         */
        public String genSign(TreeMap<String, Object> sortedParams) {
            StringBuffer buffer = new StringBuffer();
            for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
                String key = entry.getKey();
                String value = (String)entry.getValue();
                if (null == value) {
                    continue;
                }
                buffer.append(key).append("=").append(value).append("&");
            }
            int length = buffer.length();
            String content = buffer.delete(length - 1, length).toString();
            String sign = getMD5(content);
            log.info("genSign:"+sign);
            return sign;
        }

        /**
         * AES加密
         *
         * @param content		明文内容
         * @param key			密钥
         * @return
         * @throws Exception
         */
        public String aesEncrypt(String content, String key) {
            if(StringUtils.isEmpty(content)) {
                return "";
            }
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(key.getBytes("UTF-8"));
                keyGen.init(128, secureRandom);
                SecretKey secretKey = keyGen.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec keySepc = new SecretKeySpec(enCodeFormat, "AES");
                Cipher cipher = Cipher.getInstance("AES");// 创建密码器
                cipher.init(Cipher.ENCRYPT_MODE, keySepc);// 初始化
                byte[] buffer = cipher.doFinal(content.getBytes("UTF-8"));

                return byte2HexString(buffer);
            } catch (Exception e) {
                log.error("aesEncrypt error, "+e.getMessage(), e);
            }
            return "";
        }

        /**
         * AES解密
         *
         * @param content		加密内容
         * @param key			密钥
         */
        public String aesDecrypt(String content, String key) {
            if(StringUtils.isEmpty(content)) {
                return "";
            }
            byte[] contentBs = hexStr2Byte(content);
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(key.getBytes("UTF-8"));
                keyGen.init(128, secureRandom);
                SecretKey secretKey = keyGen.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");
                Cipher cipher = Cipher.getInstance("AES");// 创建密码器
                cipher.init(Cipher.DECRYPT_MODE, keySpec);// 初始化
                byte[] result = cipher.doFinal(contentBs);
                return new String(result, "UTF-8");
            } catch (Exception e) {
                log.error("getMD5 error,"+e.getMessage(), e);
            }
            return "";
        }


        /**
         * 生成md5
         *
         * @param message
         * @return
         */
        private String getMD5(String message) {
            StringBuilder md5str = new StringBuilder();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] input = message.getBytes("UTF-8");
                byte[] bytes = md.digest(input);

                // 把数组每一字节换成16进制连成md5字符串
                int digital;
                for (int i = 0; i < bytes.length; i++) {
                    digital = bytes[i];
                    if (digital < 0) {
                        digital += 256;
                    }
                    if (digital < 16) {
                        md5str.append("0");
                    }
                    md5str.append(Integer.toHexString(digital));
                }
                return md5str.toString().toLowerCase();
            } catch (Exception e) {
                log.error("getMD5 error,"+e.getMessage(), e);
            }
            return "";
        }

        /**
         * 将二进制转换成16进制
         *
         * @param buffer
         * @return
         */
        private String byte2HexString(byte buffer[]) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buffer.length; i++) {
                String hex = Integer.toHexString(buffer[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
            }
            return sb.toString();
        }

        /**
         * 将16进制转换为二进制
         *
         * @param hexString
         * @return
         */
        private byte[] hexStr2Byte(String hexString) {
            if (hexString.length() < 1) {
                return null;
            }
            byte[] result = new byte[hexString.length() / 2];
            for (int i = 0; i < hexString.length() / 2; i++) {
                int high = Integer.parseInt(hexString.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexString.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }
            return result;
        }
    }
}
