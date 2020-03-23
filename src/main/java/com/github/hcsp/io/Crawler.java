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

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
        List<String> list = new ArrayList<>();
        list.add("number,author,title");
        int index = 0;

        try {
            //System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream is = entity1.getContent();
            String body = IOUtils.toString(is, StandardCharsets.UTF_8);
            JSONArray jsonArray = JSON.parseArray(body);

            for (Object element : jsonArray) {
                if (index == n) {
                    break;
                }
                JSONObject jsObject = (JSONObject) element;
                int number = jsObject.getIntValue("number");
                String title = jsObject.getString("title");
                JSONObject userObject = (JSONObject) jsObject.get("user");
                String author = userObject.getString("login");

                list.add(number + "," + author + "," + title);

                index++;
            }
            for (String line : list) {
                System.out.println(line);
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } finally {
            response1.close();
        }

    }

    public static void main(String[] args) throws IOException {
        File tmp = File.createTempFile("csv", "");
        savePullRequestsToCSV("golang/go", 10, tmp);
    }
}
