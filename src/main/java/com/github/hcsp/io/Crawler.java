package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream in = httpEntity.getContent();
            String content = IOUtils.toString(in, StandardCharsets.UTF_8);
            JSONArray jsonArray = JSON.parseArray(content);
            FileUtils.writeStringToFile(csvFile, "number,author,title\n", StandardCharsets.UTF_8);
            for (int i = 0; i < n; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                int number = jsonObject.getInteger("number");
                String author = jsonObject.getJSONObject("user").getString("login");
                String title = jsonObject.getString("title");
                String line = String.format("%s,%s,%s\n", number, author, title);
                FileUtils.writeStringToFile(csvFile, line, StandardCharsets.UTF_8, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Crawler.savePullRequestsToCSV("golang/go", 2, new File("./a.txt"));
    }
}
