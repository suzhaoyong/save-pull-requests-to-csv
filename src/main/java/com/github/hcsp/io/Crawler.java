package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.nio.charset.Charset;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        InputStream is = httpResponse.getEntity().getContent();
        String content = IOUtils.toString(is, Charset.defaultCharset());

        StringBuilder sb = new StringBuilder("number,author,title\n");
        OutputStream os = new FileOutputStream(csvFile);
        JSONArray jsonArray = JSON.parseArray(content);
        for (int i = 0; i < n; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String number = String.valueOf(jsonObject.get("number"));
            String title = String.valueOf(jsonObject.get("title"));
            String[] segments = String.valueOf(((JSONObject) jsonObject.get("user")).get("html_url")).split("/");
            String author = segments[segments.length - 1];
            sb.append(number).append(",").append(author).append(",").append(title).append("\n");
        }
        System.out.println(sb);
        IOUtils.write(sb, os, Charset.defaultCharset());
    }
}
