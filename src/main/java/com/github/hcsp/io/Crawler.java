package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crawler {
    public static final String GITHUB_PULL_REQUEST_URL_HEAD = "https://api.github.com/repos/";
    public static final String GITHUB_PULL_REQUEST_URL_FOOT = "/pulls";

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<String> list = getFirstPageOfPullRequests(repo);
        list.add(0, "number,author,title");
        Files.write(csvFile.toPath(), list.subList(0, n + 1));
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<String> getFirstPageOfPullRequests(String repo) throws IOException {
        //设置代理IP、端口、协议（请分别替换）
        //HttpHost proxy = new HttpHost("127.0.0.1", 1080, "http");
        //把代理设置到请求配置
        /*RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .build();*/
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        String url = GITHUB_PULL_REQUEST_URL_HEAD + repo + GITHUB_PULL_REQUEST_URL_FOOT;
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        List<String> gitHubPullRequestList = new ArrayList<>();
        try {
            HttpEntity entity = response.getEntity();

            InputStream inputStream = entity.getContent();
            String html = IOUtils.toString(inputStream, "UTF-8");
            List<Map<String, Object>> pulls = (List) JSON.parse(html);
            for (Map<String, Object> pull :
                    pulls) {
                int number = (int) pull.get("number");
                String title = pull.get("title").toString();
                Map<String, Object> user = (Map<String, Object>) pull.get("user");
                String login = user.get("login").toString();
                String gitHubPullRequestString = number + "," + login + "," + title;
                gitHubPullRequestList.add(gitHubPullRequestString);
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return gitHubPullRequestList;
    }

    public static void main(String[] args) throws IOException {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        File testFile = new File(projectDir, "target/test.csv");
        savePullRequestsToCSV("golang/go", 10, testFile);
    }
}

