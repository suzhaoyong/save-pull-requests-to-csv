package com.github.hcsp.io;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<String> pullRequests = getPullRequests(repo, n);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));
        for (String pullRequest : pullRequests) {
            bufferedWriter.write(pullRequest);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

    }


    public static List<String> getPullRequests(String repo, int n) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        List<String> list = new ArrayList<>();
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            InputStream inputStream = response.getEntity().getContent();
            String contentString = IOUtils.toString(inputStream, Charset.defaultCharset());
            JSONArray jsonArray = JSON.parseArray(contentString);
            int end = n;
            if (jsonArray.size() < n) {
                end = jsonArray.size();
            }
            list.add(String.join(",", "number", "author", "title"));
            for (int i = 0; i < end; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String number = jsonObject.getString("number");
                String title = jsonObject.getString("title");
                String author = jsonObject.getJSONObject("user").getString("login");
                list.add(String.join(",", number, author, title));
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        List<String> pullRequests = getPullRequests("gradle/gradle", 5);
        System.out.println("pullRequests = " + pullRequests);
    }
}
