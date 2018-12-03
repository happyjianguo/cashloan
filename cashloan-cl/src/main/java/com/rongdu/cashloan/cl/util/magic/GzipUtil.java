package com.rongdu.cashloan.cl.util.magic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by szb on 18/11/23.
 */
public class GzipUtil {
    public static String uncompress(InputStream gzippedResponse) throws IOException {

        InputStream decompressedResponse = new GZIPInputStream(gzippedResponse);
        Reader reader = new InputStreamReader(decompressedResponse, "UTF-8");
        StringWriter writer = new StringWriter();

        char[] buffer = new char[10240];
        for(int length = 0; (length = reader.read(buffer)) > 0;){
            writer.write(buffer, 0, length);
        }

        writer.close();
        reader.close();
        decompressedResponse.close();
        gzippedResponse.close();

        return writer.toString();
    }

    public static void main(String[] args) throws IOException {
        // {mobile} 申请人的手机号
        // {task_id} 用户授权认证，创建运营商采集任务的任务ID
        // 请注意修改
        // 样例：URL url = new URL("https://api.51datakey.com/carrier/v3/mobiles/18116270000/mxdata?task_id=008ef370-5f11-11e6-909c-00163e004a23“);
        URL url = new URL("https://api.51datakey.com/carrier/v3/mobiles/13701794314/mxdata?task_id=164b8900-a035-11e8-b9d5-00163e0f4b67");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
        connection.setRequestProperty("Accept", "application/json");

        // value 为魔蝎分配的token
        // 请注意修改
        // 样例：connection.setRequestProperty("Authorization", "token 090b2fd02d034dbea409bbd5f9900bc2");
        connection.setRequestProperty("Authorization", "token " + "8ef655524dcf4a0c8bdb8d08bcb7ef41");
        connection.connect();
        System.out.println(uncompress(connection.getInputStream()));
        connection.disconnect();

    }
}
