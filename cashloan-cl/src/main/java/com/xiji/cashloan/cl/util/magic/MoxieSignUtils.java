package com.xiji.cashloan.cl.util.magic;

import org.apache.commons.codec.binary.Base64;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * 魔蝎风控云签名工具类
 * 重新生成公钥和私钥(每次执行公钥和私钥会变化,需要更新moxie平台上的公钥)
 * Created by szb on 18/11/29.
 */
public class MoxieSignUtils {
    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String ALGORITHM_RSA = "RSA";
    public static final String ALGORITHM_SHA1_WITH_RSA = "SHA1withRSA";

    /**
     * 签名入口
     *
     * @param paramsMap  请求参数
     * @param privateKey RSA私钥
     * @return
     */
    public static String signSHA1WithRSA(Map<String, String> paramsMap, String privateKey) {
        String signStr = getSortMapParamStr(paramsMap);
        return signWithRSA(signStr, privateKey, ALGORITHM_SHA1_WITH_RSA);
    }

    /**
     * 验证签名入口
     *
     * @param paramsMap 响应参数
     * @param publicKey RSA公钥
     * @return
     */
    public static boolean verifySHA1WithRSA(Map<String, String> paramsMap, String publicKey) {
        String signStr = getSortMapParamStr(paramsMap);
        System.out.println("signStr:" + signStr);
        String sign = paramsMap.get("sign");
        return verifyWithRSA(signStr, sign, publicKey, ALGORITHM_SHA1_WITH_RSA);
    }


    /**
     * SHA1withRSA签名
     *
     * @param source     签名字符串
     * @param privateKey (Base64)RSA私钥
     * @return Base64编码
     */
    private static String signSHA1WithRSA(String source, String privateKey) {
        return signWithRSA(source, privateKey, ALGORITHM_SHA1_WITH_RSA);
    }

    /**
     * SHA1withRSA签名验证
     *
     * @param source    签名字符串
     * @param sign      (Base64)签名
     * @param publicKey (Base64)RSA公钥
     * @return
     */
    private static boolean verifySHA1WithRSA(String source, String sign, String publicKey) {
        return verifyWithRSA(source, sign, publicKey, ALGORITHM_SHA1_WITH_RSA);
    }


    /**
     * RSA签名
     *
     * @param source     待签名字符串
     * @param privateKey (Base64)RSA私钥
     * @param algorithm  具体签名算法
     * @return (Base64)签名
     */
    private static String signWithRSA(String source, String privateKey, String algorithm) {
        String result = null;
        try {
            Base64 base64 = new Base64();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(base64.decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PrivateKey pk = keyFactory.generatePrivate(spec);
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(pk);
            signature.update(source.getBytes(CHARSET_UTF_8));
            result = new String(base64.encode(signature.sign()));
        } catch (Exception e) {
            System.out.println("RSA 签名出错！");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 验证RSA签名
     *
     * @param source    原字符串
     * @param sign      签名
     * @param publicKey (Base64)RSA公钥
     * @param algorithm 具体签名算法
     * @return
     */
    private static boolean verifyWithRSA(String source, String sign, String publicKey, String algorithm) {
        boolean result = false;
        try {
            Base64 base64 = new Base64();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(base64.decode(publicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            PublicKey pk = keyFactory.generatePublic(spec);
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(pk);
            signature.update(source.getBytes(CHARSET_UTF_8));
            result = signature.verify(base64.decode(publicKey));
        } catch (Exception e) {
            System.out.println("验证RSA签名出错！");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成RSA密钥
     *
     * @param keySize 密钥长度
     * @throws Exception
     */
    public static KeyPair generateRSAKey(int keySize) throws Exception {
        Base64 base64 = new Base64();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        kpg.initialize(keySize);
        KeyPair kp = kpg.generateKeyPair();
        System.out.println("privateKey:\n"+new String(base64.encode(kp.getPrivate().getEncoded())));
        System.out.println("publicKey:\n"+new String(base64.encode(kp.getPublic().getEncoded())));
        return kp;
    }

    private static String getSortMapParamStr(Map<String, String> data) {
        SortedMap<String, String> map = new TreeMap<String, String>();
        for (String key : data.keySet()) {
            if ("sign".equals(key)) {
                continue;
            }
            if (data.get(key)!=null && data.get(key).trim()!="") {
                map.put(key, data.get(key));
            }
        }
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        StringBuffer str = new StringBuffer();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = map.get(key);
            str.append(key).append("=").append(value).append("&");
        }
        if (str.length() > 0) {
            return str.substring(0, str.length() - 1);
        }
        return "";
    }

    /**
     * 执行main方法,控制台会输出生成的公钥和私钥
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//        generateRSAKey(2048);
    }
}
