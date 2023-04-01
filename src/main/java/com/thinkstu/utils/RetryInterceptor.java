package com.thinkstu.utils;

import okhttp3.*;

import java.io.*;

public class RetryInterceptor implements Interceptor {
    private int maxRetryCount; // 最大重试次数
    private int retryInterval; // 重试间隔

    public RetryInterceptor(int maxRetryCount, int retryInterval) {
        this.maxRetryCount = maxRetryCount;
        this.retryInterval = retryInterval;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        int retryCount = 0;
        Response response = null;
        IOException lastException = null;
        do {
            try {
                response = chain.proceed(chain.request());
                // 如果响应成功了，直接返回响应结果
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (IOException e) {
                lastException = e;
            }
            retryCount++;
            if (retryCount <= maxRetryCount) {
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException();
                }
            } else {
                break;
            }
        } while (retryCount <= maxRetryCount);
        // 如果重试次数已达到最大值或者出现了不可重试的异常，则抛出异常
        if (lastException != null) {
            throw lastException;
        } else {
            throw new IOException("Unknown error");
        }
    }
}
