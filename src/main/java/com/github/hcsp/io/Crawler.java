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
import org.apache.http.util.EntityUtils;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        BufferedWriter writer = null;
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            String pullRequestJson = IOUtils.toString(is, "UTF-8");
            JSONArray pulls = JSON.parseArray(pullRequestJson);
            writer = new BufferedWriter(new FileWriter(csvFile));
            StringBuilder sb = new StringBuilder();
            sb.append("number,author,title\n");
            writer.write(sb.toString());
            for (int i = 0; i < n; i++) {
                JSONObject pullRequest = pulls.getJSONObject(i);
                int number = pullRequest.getInteger("number");
                String title = pullRequest.getString("title");
                String author = pullRequest.getJSONObject("user").getString("login");
                sb.setLength(0);
                sb.append(number).append(",").append(author).append(",").
                        append("\"").append(title).append("\"").append("\n");
                System.out.println(sb.toString());
                writer.write(sb.toString());
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
            if (writer != null) {
                writer.close();
            }
        }
    }
}
