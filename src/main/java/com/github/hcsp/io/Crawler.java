package com.github.hcsp.io;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static void getFirstPageOfPullRequests(String repo, Integer n, File file) throws IOException {
        // 创建一个default 客户端
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 发起了一个http Get请求
        StringBuilder target = new StringBuilder("https://api.github.com/repos/" + repo + "/pulls");
        HttpGet httpGet = new HttpGet(String.valueOf(target));
        // 执行这个请求拿到response
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        // 传输的类型
        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity1 = response.getEntity();
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            JSONArray JSONArray = JSON.parseArray(html);
            traverse(JSONArray, n, file);
        } finally {
            response.close();
        }
    }

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        getFirstPageOfPullRequests(repo, n, csvFile);
    }

    public static void traverse(JSONArray issuesInfoList, Integer n, File file) throws IOException {
        List<String> linesList = new ArrayList<>();
        linesList.add("number,author,title");
        for (int i = 0; i < n; i++) {
            JSONObject account = (JSONObject) issuesInfoList.getJSONObject(i).get("user");
            Integer number = (Integer) issuesInfoList.getJSONObject(i).get("number");
            String title = (String) issuesInfoList.getJSONObject(i).get("title");
            String user = account.getString("login");
            String singleLines = number + "," + user + "," + title;
            linesList.add(singleLines);
            Files.write(file.toPath(), linesList);
        }
        System.out.println(linesList);
    }

    public static void main(String[] args) throws IOException {
//        savePullRequestsToCSV("gradle/gradle", 3, new File("./test3.csv"));
        savePullRequestsToCSV("golang/go", 5, new File("./test8.csv"));
    }
}



