package com.github.hcsp.io;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private static HttpClient httpClient = HttpClients.createDefault();
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36";

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String result = httpGet(repo);
        JSONArray array = JSON.parseArray(result);
        List<String> list = new ArrayList<>();
        int length = array.size() < n ? array.size() : n;

        list.add("number,author,title");
        for (int i = 0; i < length; ++i) {
            JSONObject obj = (JSONObject) array.get(i);
            int number = obj.getInteger("number");
            String author = obj.getJSONObject("user").getString("login");
            String title = obj.getString("title");
            list.add(number + "," + author + "," + title);
        }

        Files.write(csvFile.toPath(), list);
    }

    private static String httpGet(String repo) throws IOException {
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("User-Agent", USER_AGENT);

        HttpResponse execute = httpClient.execute(httpGet);
        HttpEntity entity = execute.getEntity();
        InputStream content = entity.getContent();
        return IOUtils.toString(content, StandardCharsets.UTF_8);
    }
}
