package com.zrk1000.crawler.visitor.okhttp;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by moonie on 2017/8/22.
 */
public class OKHttpPool {

    private static Logger logger = LoggerFactory.getLogger(OKHttpPool.class);

    private OkHttpClient.Builder clientBuilder;
    private ConnectionPool pool;
    private int maxIdleConnections = 5000;
    private long keepAliveDuratio = 60;

    public OKHttpPool() {
        initOkHttpClient();
    }

    public OKHttpPool(int maxIdleConnections, long keepAliveDuratio) {
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDuratio = keepAliveDuratio;
        initOkHttpClient();
    }

    private void initOkHttpClient(){
        this.pool = new ConnectionPool(maxIdleConnections, keepAliveDuratio, TimeUnit.SECONDS);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HostnameVerifier hostnameVerifier = (s, sslSession) -> true;
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } ;
        TrustManager[] trustAllCerts = new TrustManager[]{trustManager};

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error(e.getMessage(),e);
        }
        clientBuilder = builder
                .hostnameVerifier(hostnameVerifier)
                .sslSocketFactory(sc.getSocketFactory(), trustManager)
                .hostnameVerifier((s, sslSession) -> true)
                .connectionPool(pool);
    }

    public OkHttpClient.Builder getClientBuilder() {
        return clientBuilder;
    }

    public void setClientBuilder(OkHttpClient.Builder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
        initOkHttpClient();
    }

    public long getKeepAliveDuratio() {
        return keepAliveDuratio;
    }

    public void setKeepAliveDuratio(long keepAliveDuratio) {
        this.keepAliveDuratio = keepAliveDuratio;
        initOkHttpClient();
    }
}
