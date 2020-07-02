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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            List<String> datalist = new ArrayList<>();
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            String data = IOUtils.toString(is, StandardCharsets.UTF_8);
            JSONArray prlist = JSON.parseArray(data);
            datalist.add("number,author,title");
            for (int i = 0; i < n; i++) {
                int number = prlist.getJSONObject(i).getInteger("number");
                String title = prlist.getJSONObject(i).getString("title");
                JSONObject user = (JSONObject) prlist.getJSONObject(i).get("user");
                String author = user.getString("login");
                datalist.add(number + "," + author + "," + title);
            }
            Files.write(csvFile.toPath(), datalist);
        }
    }
}
