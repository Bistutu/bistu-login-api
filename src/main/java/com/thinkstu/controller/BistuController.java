package com.thinkstu.controller;

import com.thinkstu.service.*;
import com.thinkstu.utils.*;
import lombok.extern.slf4j.*;
import okhttp3.RequestBody;
import okhttp3.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/bistu")
public class BistuController {
    @Autowired
    private LoginService loginService;
    String user_agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36";

    @Autowired
    OkHttpClient client;
    Request request;
    Response response;


    /**
     * 单纯登录，获取初始 Cookie
     * */
    @RequestMapping(value = "login", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, String> login(String username, String password) {
        Map<String, String> cookies = null;
        try {
            cookies = loginService.login(username, password);
        } catch (Exception e) {
            // 什么也不做
        }
        if (cookies == null) {
            throw new RuntimeException("登陆失败，cookies返回为null");
        }
        return cookies;
    }

    /**
     * 实名认证
     */
    @RequestMapping(value = "verified", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, String> Verified(String username, String password) throws IOException {
        Map<String, String> initialCookies = null;
        try {
            initialCookies = loginService.login(username, password);
        } catch (Exception e) {
            // 什么也不做
        }
        if (initialCookies == null) {
            throw new RuntimeException("登陆失败，cookies返回为null");
        }
        MyMap<String, String> cookie = MyMap.mapConvertMymap(initialCookies);
        // 再次发送请求，并获取实名
        Request request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/emaphome/portal/index.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/")
                .addHeader("User-Agent", user_agent)
                .build();
        Response              response = client.newCall(request).execute();
        Document              document = Jsoup.parse(response.body().string());
        String                name     = document.getElementsByClass("p_header_username").first().text();
        MyMap<String, String> result   = new MyMap<>();
        result.put("id", username);
        result.put("name", name);
        return result;
    }

    /**
     * 获取空教室页 Cookie
     * */
    @GetMapping("/empty")
    public Map<String, String> getEmptyCookie(String username, String password) throws IOException {

        // 教务网登录 cookie
        Map<String, String> initialCookie = login(username, password);
        // map 转 myMap
        MyMap<String, String> cookie     = MyMap.mapConvertMymap(initialCookie);
        String                rootCookie = cookie.toString();

        // 第二次发送请求
        MediaType   mediaType = MediaType.parse("application/json");
        RequestBody body      = RequestBody.create(mediaType, "");
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/jwpubapp/gjhwhController/queryAppGjhMapping.do")
                .method("POST", body)
                .addHeader("Cookie", cookie.toString())
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/kxjas/*default/index.do?EMAP_LANG=zh")
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第三次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/jwpubapp/modules/bb/cxjwggbbdqx.do")
                .method("POST", body)
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/kxjas/*default/index.do?EMAP_LANG=zh")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第四次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/funauthapp/api/getAppConfig/kxjas-4768402106681759.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第五次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/emappagelog/config/kxjas.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第六次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/emapcomponent/schema/getList.do")
                .method("POST", body)
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/kxjas/*default/index.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        String emptyCookie = cookie.emptyCookieForAuthorized();
        log.info("===========》", emptyCookie);
        Map<String, String> map = new MyMap<>();
        map.put("rootCookie", rootCookie);
        map.put("emptyCookie", emptyCookie);
        // 开始获取
        return map;
    }

    /**
     * 获取学期课程表
     * */
    @GetMapping("/curriculum")
    public String getCurriculum(String username, String password) throws IOException {

        // 教务网登录 cookie
        Map<String, String>   initialCookie = login(username, password);
        MyMap<String, String> cookie        = MyMap.mapConvertMymap(initialCookie);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        // 第二次发送请求
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/i18n.do?appName=emaphome&EMAP_LANG=zh")
                .addHeader("Cookie", cookie.toString())
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/emaphome/portal/index.do")
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第三次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/emaphome/appShow.do")
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/emaphome/portal/index.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第四次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/funauthapp/api/getAppConfig/wdkb-4770397878132218.do")
                .addHeader("Cookie", cookie.toString())
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/wdkb/*default/index.do?EMAP_LANG=zh")
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第五次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/jwpubapp/modules/bb/cxjwggbbdqx.do")
                .method("POST", RequestBody.create(mediaType, "SFQY=1&APP=4770397878132218"))
                .addHeader("Cookie", cookie.toString())
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/wdkb/*default/index.do?EMAP_LANG=zh")
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第六次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/emappagelog/config/wdkb.do")
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/wdkb/*default/index.do?EMAP_LANG=zh")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        cookie.refresh(response);

        // 第七次
        request = new Request.Builder()
                .url("https://jwxt.bistu.edu.cn/jwapp/sys/wdkb/modules/xskcb/cxxszhxqkb.do")
                // SKZC=5 表示获取第几周的课程表
//                .method("POST", RequestBody.create(mediaType, "XNXQDM=2022-2023-2&SKZC=5"))
                .method("POST", RequestBody.create(mediaType, "XNXQDM=2022-2023-2"))
                .addHeader("Referer", "https://jwxt.bistu.edu.cn/jwapp/sys/wdkb/*default/index.do?EMAP_LANG=zh")
                .addHeader("Cookie", cookie.toString())
                .addHeader("User-Agent", user_agent)
                .build();
        response = client.newCall(request).execute();
        // 开始获取
        String curriculum = response.body().string();
        log.info(curriculum);
        return curriculum;
    }
}
