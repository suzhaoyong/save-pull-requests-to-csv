package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        List<String> list = new ArrayList<>();
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new HttpResponseException(statusCode, response.getStatusLine().toString());
            }

            InputStream bodyStream = response.getEntity().getContent();
            String body = IOUtils.toString(bodyStream, "UTF-8");
            JSONArray array = JSON.parseArray(body);

            list.add("number,author,title");
            for (int i = 0; i < n; i++) {
                JSONObject jsonObject = (JSONObject) array.get(i);
                String title = jsonObject.getString("title");
                int number = jsonObject.getIntValue("number");
                String author = jsonObject.getJSONObject("user").getString("login");
                String pullRequest = number + "," + author + "," + title;
                list.add(pullRequest);
            }


        } finally {
            response.close();
        }

        if (list.size() > 0) {
            Files.write(Paths.get(String.valueOf(csvFile)), list, Charset.forName("UTF-8"));
        }
    }
}
