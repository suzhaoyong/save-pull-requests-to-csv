package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    private static final int PER_PAGE = 30;

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            writer.writeNext(new String[]{"number", "author", "title"});
            int page = n / PER_PAGE + 1;
            int num = n;
            for (int i = 1; i <= page; i++) {
                num -= (PER_PAGE * (i - 1));
                savePullRequestsToCSV("https://api.github.com/repos/" + repo + "/pulls?page=" + page, writer, num);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void savePullRequestsToCSV(String url, CSVWriter writer, int n) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            HttpEntity entity1 = response1.getEntity();
            InputStream content = entity1.getContent();

            String html = IOUtils.toString(content, StandardCharsets.UTF_8);
            JSONArray objects = JSON.parseArray(html);
            List<String[]> data = new ArrayList<>();
            int num = n;
            for (Object object : objects) {
                if (num == 0) {
                    break;
                }
                String[] line = new String[3];
                line[2] = (String) ((JSONObject) object).get("title");
                Object user = ((JSONObject) object).get("user");
                line[1] = (String) ((JSONObject) user).get("login");
                line[0] = String.valueOf(((JSONObject) object).get("number"));
                data.add(line);
                num--;
            }
            writer.writeAll(data);
        }
    }

}
