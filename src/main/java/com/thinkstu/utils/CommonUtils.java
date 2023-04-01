package com.thinkstu.utils;

import java.io.*;
import java.util.regex.*;

/**
 * @author : ThinkStu
 * @since : 2023/3/31, 15:35, 周五
 **/
public class CommonUtils {
    static Pattern alibaba_pattern = Pattern.compile(".*Alibaba.*");

    public static Integer getProxyPort() {
        try {
            Process        process = Runtime.getRuntime().exec("grep DMI /var/log/dmesg");
            BufferedReader reader  = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String         line    = reader.readLine();
            if (alibaba_pattern.matcher(line).matches()) {
                return 10808;    // 是阿里云
            }
        } catch (Exception e) {
        }
        return 7890;    // 不是阿里云
    }

}
