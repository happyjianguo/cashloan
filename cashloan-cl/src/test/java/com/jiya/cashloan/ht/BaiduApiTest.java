package com.jiya.cashloan.ht;

import static com.xiji.cashloan.rc.controller.SceneBusinessLogController.logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiji.cashloan.cl.model.dsdata.FaceCheckIdCardRequest;
import com.xiji.cashloan.cl.model.dsdata.FaceCheckOcrRequest;
import com.xiji.cashloan.cl.model.dsdata.facecheck.FaceCheckIdCardResult;
import com.xiji.cashloan.cl.model.dsdata.facecheck.FaceCheckReq;
import com.xiji.cashloan.cl.model.dsdata.facecheck.FaceCheckResult;
import com.xiji.cashloan.cl.model.dsdata.util.FaceCheckUtil;
import com.xiji.cashloan.cl.util.RSACoder;
import com.xiji.cashloan.core.common.util.Base64;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sun.misc.BASE64Encoder;
import tool.util.StringUtil;

/**
 * @Auther: king
 * @Date: 2019/1/30 11:53
 * @Description:
 */
public class BaiduApiTest {

    public static void main(String[] args) throws Exception {
//        apiTest();
//        FaceCheckOcrRequest();
        FaceCheckIdCardRequest();
    }

//    public static void apiTest() {
//        String requrl = "http://rryqo.com/finance/v1/face/match?applyNo=124";
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("image1", fileToImage("/Users/51NB/Desktop/wxbIdcard.png"));
//        jsonObject.put("image2", fileToImage("/Users/51NB/Desktop/wxbIdcard.png"));
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
//
//        String signStr = Base64.encode(RSACoder.encryptByPrivateKey("CH13IVR8S124".getBytes(), Base64.decode("")));
//        System.out.println(signStr);
//
//        Request request = new Request.Builder().url(requrl)
//            .addHeader("channelNo", "CH13IVR8S")
//            .addHeader("signStr", signStr)
//            .post(requestBody).build();
//        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
//        try {
//            ResponseBody result = okHttpClient.newCall(request).execute().body();
//            JSONObject jsonObject2 = JSON.parseObject(result.string());
//            System.out.println(jsonObject2.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String signStr = Base64.encode(RSACoder.encryptByPrivateKey("CH13IVR8S124".getBytes(), Base64.decode("MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAoQbUJfG8h63o2klN3InuK1qUetS71O0YINFlHyZzzKmRBCgNyvuDt8ZuCjB9Zrexk+FNOeUg2dGV8XSCZKwLmwIDAQABAkEAg/C3ddvMMZQi/nEf9juiRi2zCa4ztbULlyBb7hkwuxlL+HYHln8EhgvBTGAWb596BQTmmDET1iVgDm+pWEfd2QIhANk6cX7/H4AkKr9GLlc5KMJNm7+/tJzoMTw6uETwfL1HAiEAvcRuUYY4azGhBAJmsoxSy/S0DSGYZlohMN+FYjSRmQ0CIH+257GVx2xsVyGb3nTzqy4JuO9Ug5jYvtG9aEdH6N7TAiEAgoeV9l+jeSBHB/H63/+jiAUGwC2GnYiLYgmtvtI4ABUCIQC1BoDi3sip+YcY3gw6+SbChaRNAcfZVoeJK60ZM5+xww==")));
//    }

    public static void testImage() throws Exception {

        String signStr = Base64.encode(RSACoder.encryptByPrivateKey("CH13IVR8S125".getBytes(), Base64.decode("MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAoQbUJfG8h63o2klN3InuK1qUetS71O0YINFlHyZzzKmRBCgNyvuDt8ZuCjB9Zrexk+FNOeUg2dGV8XSCZKwLmwIDAQABAkEAg/C3ddvMMZQi/nEf9juiRi2zCa4ztbULlyBb7hkwuxlL+HYHln8EhgvBTGAWb596BQTmmDET1iVgDm+pWEfd2QIhANk6cX7/H4AkKr9GLlc5KMJNm7+/tJzoMTw6uETwfL1HAiEAvcRuUYY4azGhBAJmsoxSy/S0DSGYZlohMN+FYjSRmQ0CIH+257GVx2xsVyGb3nTzqy4JuO9Ug5jYvtG9aEdH6N7TAiEAgoeV9l+jeSBHB/H63/+jiAUGwC2GnYiLYgmtvtI4ABUCIQC1BoDi3sip+YcY3gw6+SbChaRNAcfZVoeJK60ZM5+xww==")));
        HttpPost httpPost = new HttpPost("http://rryqo.com/finance/v1/face/match?applyNo=125");
        httpPost.setHeader("channelNo","CH13IVR8S");
        httpPost.setHeader("signStr", signStr);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("image1", getImageStr("/unit/word/self/WechatIMG5.jpg"));
        paramMap.put("image2", getImageStr("/unit/word/self/WechatIMG9.jpeg"));
        String body = JSON.toJSONString(paramMap);
        ContentType contentType = ContentType.create("application/json", "utf-8");
        httpPost.setEntity(new StringEntity(body, contentType));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            CloseableHttpResponse res = httpClient.execute(httpPost);

            System.out.println(convertStreamToString(res.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        return sb.toString();
    }

    public static void FaceCheckOcrRequest() {
        String ocrFrontImg = "/unit/word/self/WechatIMG5.jpg";
        String idcardImage = "/unit/word/self/WechatIMG9.jpeg";
        String orderNo = FaceCheckUtil.getSeqNumber();
        FaceCheckReq req = new FaceCheckReq();
        req.setFrontImgPath(ocrFrontImg);
        req.setLivingImgPath(idcardImage);
        FaceCheckResult result = new FaceCheckResult();
        FaceCheckOcrRequest request = new FaceCheckOcrRequest(ocrFrontImg,idcardImage,orderNo);
        try {
            String str = request.request();
            System.out.println("执行结果==> "  + str);

            JSONObject resultJson = JSONObject.parseObject(str);
            if (resultJson.get("errorCode") != null) {
                String errorCode = resultJson.get("errorCode").toString();
                result.setCode(errorCode);
            }
            if (resultJson.get("score") != null) {
                result.setScore(NumberUtils.toDouble(resultJson.get("score").toString(),0.0));
            }
            result.setReqParams(JSONObject.toJSONString(req));
            result.setTaskId(orderNo);
            result.setReturnParams(str);
            result.setFaceModel(FaceCheckUtil.model_kFace);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(JSONObject.toJSONString(result));
    }

    public static void FaceCheckIdCardRequest() throws Exception {
        String idcardImage = "/unit/word/self/WechatIMG5.jpg";
        String orderNo = FaceCheckUtil.getSeqNumber();
        FaceCheckIdCardResult result = new FaceCheckIdCardResult();
        FaceCheckIdCardRequest faceCheckIdCardRequest = new FaceCheckIdCardRequest(idcardImage,orderNo);
        String requestStr = faceCheckIdCardRequest.request();
        logger.info("kFaceCheckIDCard ocr返回结果-->" + requestStr);
        JSONObject resultJson = JSONObject.parseObject(requestStr);
        if (StringUtil.isNotBlank(resultJson)) {
            if ("normal".equals(resultJson.getString("imageStatus"))){
                JSONObject wordResult = resultJson.getJSONObject("wordResults");
                if (wordResult != null) {
                    result.setName(wordResult.getString("姓名"));
                    result.setIdNum(wordResult.getString("公民身份号码"));
                    result.setAddress(wordResult.getString("住址"));
                }
            }
        }
        System.out.println(result.getAddress());
    }

}
