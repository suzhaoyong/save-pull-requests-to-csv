package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws Exception {
        CloseableHttpResponse response = null;
        String res = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=" + n);
            response = httpclient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            res = EntityUtils.toString(httpEntity);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        if (res != null) {
            JSONArray jsonArray = JSON.parseArray(res);
            if (jsonArray != null) {
                List<String> gitHubPullRequests = new ArrayList<>();
                gitHubPullRequests.add("number,author,title");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int number = jsonObject.getInteger("number");
                    String title = jsonObject.getString("title");
                    String login = jsonObject.getJSONObject("user").getString("login");
                    gitHubPullRequests.add(number + "," + login + "," + title);
                }
                Files.write(Paths.get(csvFile.toURI()), gitHubPullRequests);
            }
        }
    }
}
