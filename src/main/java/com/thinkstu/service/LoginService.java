package com.thinkstu.service;

import com.thinkstu.utils.*;

import java.util.Map;

public interface LoginService {
    Map<String, String> login(String login_url, String username, String password) throws Exception;

    Map<String, String> login(String username, String password) throws Exception;
}
