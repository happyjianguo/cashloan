package com.rongdu.cashloan.cl.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.*;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by szb on 18/11/16.
 */
public abstract class BaseCreditRequest {
    private String host;
    protected String authToken;
    protected Map<String, Object> payload;
    private static CloseableHttpClient httpClient;
    private static RequestConfig requestConfig;
    private static final int CONNECTION_REQUEST_TIMEOUT = 2000;
    private static final int CONNECT_TIMEOUT = 8000;
    private static final int SOCKET_TIMEOUT = 20000;
    private static final String AUTH_TOKEN = "auth_token";
    protected static long timestamp;

    public BaseCreditRequest(String host, String authToken, long timestamp) {
        this.host = host;
        this.authToken = authToken;
        this.timestamp = timestamp;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public String request() throws Exception {
        HttpPost httpPost = new HttpPost(this.host);
        String responseStr = null;
        CloseableHttpResponse resp = null;

        try {
            HttpEntity e = this.handle();
            if(StringUtils.isNotBlank(authToken)) {
                httpPost.setHeader(AUTH_TOKEN, authToken);
            }
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(e);
            resp = httpClient.execute(httpPost);
            InputStream respIs = resp.getEntity().getContent();
            responseStr = this.convertStreamToString(respIs);
        } finally {
            if(resp != null) {
                try {
                    EntityUtils.consume(resp.getEntity());
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }

        }

        return responseStr;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while((line = reader.readLine()) != null) {
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

    protected abstract HttpEntity handle() throws Exception;

    static {
        try {
            SSLContext e = SSLContexts.custom().loadTrustMaterial((KeyStore)null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(e, new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"}, (String[])null, NoopHostnameVerifier.INSTANCE);
            Registry registry = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslsf).build();
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(200).setMaxLineLength(2000).build();
            ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).setMessageConstraints(messageConstraints).build();
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            cm.setDefaultConnectionConfig(connectionConfig);
            cm.setDefaultSocketConfig(socketConfig);
            cm.setMaxTotal(200);
            cm.setDefaultMaxPerRoute(10);
            httpClient = HttpClients.custom().setConnectionManager(cm).evictExpiredConnections().evictIdleConnections(3L, TimeUnit.SECONDS).setRetryHandler(new HttpRequestRetryHandler() {
                public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {
                    return executionCount >= 3?false:(e instanceof NoHttpResponseException | e instanceof HttpHostConnectException ?true:e instanceof ClientProtocolException);
                }
            }).build();
            requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        } catch (KeyStoreException | KeyManagementException | NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        }

    }

}
