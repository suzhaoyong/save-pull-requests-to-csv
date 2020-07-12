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
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            HttpEntity entity = response.getEntity();
            String str = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
            JSONArray jsonArray = JSON.parseArray(str);
            List<String> list = new ArrayList<>();

            list.add("number,author,title");
            for (int i = 0; i < n; i++) {
                if (n >= jsonArray.size()) {
                    return;
                }
                Object obj = jsonArray.get(i);
                String numStr = ((JSONObject) obj).get("number").toString();
                Object authorObject = ((JSONObject) obj).get("user");
                String author = ((JSONObject) authorObject).get("login").toString();
                String title = ((JSONObject) obj).get("title").toString();
                list.add(numStr + "," + author + "," + title);
            }
            Files.write(csvFile.toPath(), list);
        } finally {
            response.close();
        }
    }
}
