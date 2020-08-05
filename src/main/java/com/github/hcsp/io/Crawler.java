package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
        CloseableHttpResponse response = httpClient.execute(httpGet);

        System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();
        InputStream html = entity.getContent();
        String body = IOUtils.toString(html, Charset.defaultCharset());
        JSONArray objects = JSON.parseArray(body);
        try {
            int i = 0;
            List<String> list = new ArrayList<>();
            list.add("number,author,title");
            for (Object object : objects) {
                if (i >= n) {
                    break;
                } else {
                    int num = (int) ((JSONObject) object).get("number");
                    String number = String.valueOf(num);
                    JSONObject user = (JSONObject) ((JSONObject) object).get("user");
                    String author = (String) user.get("login");
                    String title = (String) ((JSONObject) object).get("title");

                    list.add(number + "," + author + "," + title);
                    i++;
                }
            }
            Files.write(csvFile.toPath(), list, Charset.defaultCharset());
        } finally {
            response.close();
        }
    }
}
