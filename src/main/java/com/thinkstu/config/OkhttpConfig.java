package com.thinkstu.config;

import com.thinkstu.utils.*;
import okhttp3.*;
import org.springframework.context.annotation.*;

import java.util.concurrent.*;

/**
 * @author : ThinkStu
 * @since : 2023/3/31, 13:14, 周五
 **/
@Configuration
public class OkhttpConfig {
    @Bean
    OkHttpClient okHttpClient() {
        return new OkHttpClient()
                .newBuilder()
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                .addInterceptor(new RetryInterceptor(3, 1000))
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
//                .proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", CommonUtils.getProxyPort())))
                .build();
    }
}
