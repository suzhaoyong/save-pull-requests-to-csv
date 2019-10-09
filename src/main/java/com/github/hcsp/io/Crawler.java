package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String uri = MessageFormat.format("https://api.github.com/repos/{0}/pulls", repo);
        String pullRequestsJSONDate;
        List pullRequestList;
        List<String> limitedPullRequestInfoList;

        // 获取指定 repo 的 JSON 数据
        pullRequestsJSONDate = getJsonDate(uri);
        // 将 JSON 数据解析为 List
        pullRequestList = JSON.parseObject(pullRequestsJSONDate, List.class);
        // 从 List 获取指定数目的需要的信息
        limitedPullRequestInfoList = getLimitedPullRequestInfoList(pullRequestList, n);
        // 将数据写入文件
        writeDateToCSV(limitedPullRequestInfoList, csvFile);
    }

    private static void writeDateToCSV(List<String> dataList, File csvFile) throws IOException {
        dataList.add(0, "number,author,title");
        FileUtils.writeLines(csvFile, dataList);
    }

    private static String getJsonDate(String uri) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        return IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
    }

    private static String getPullRequestInfo(Map pullRequestDateMap) {
        String number = pullRequestDateMap.get("number").toString();
        String author = (String) json2Map((JSONObject) pullRequestDateMap.get("user")).get("login");
        String title = (String) pullRequestDateMap.get("title");

        return number + "," + author + "," + title;
    }

    private static List<String> getLimitedPullRequestInfoList(List pullRequestList, int n) {
        List<String> pullRequestInfoList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            pullRequestInfoList.add(getPullRequestInfo(json2Map((JSONObject) pullRequestList.get(i))));
        }

        return pullRequestInfoList;
    }

    private static Map json2Map(JSONObject jsonObject) {
        return JSONObject.toJavaObject(jsonObject, Map.class);
    }
}
