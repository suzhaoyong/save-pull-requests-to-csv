package com.github.hcsp.io;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<String> res = new ArrayList<>();
        res.add("number,author,title");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String html = "https://api.github.com/repos/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(html);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        System.out.println(response1);
        HttpEntity entity1 = response1.getEntity();
        InputStream is = entity1.getContent();
        //将input stream 变成 string
        String string = IOUtils.toString(is, "UTF-8");

        JSONArray jsonArray = JSONArray.parseArray(string);
        for (int i = 0; i < n; i++) {
            int number = jsonArray.getJSONObject(i).getInteger("number");
            String title = jsonArray.getJSONObject(i).getString("title");
            String author = jsonArray.getJSONObject(i).getJSONObject("user").getString("login");
            String content = number + "," + author + "," + title;
            res.add(content);
        }
        Files.write(csvFile.toPath(), res);

    }


    public static void main(String[] args) throws IOException {
        File tmp = File.createTempFile("csv", "");
        savePullRequestsToCSV("golang/go", 10, tmp);

    }
}
