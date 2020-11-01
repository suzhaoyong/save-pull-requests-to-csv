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

        FileOutputStream fileOutputStream = new FileOutputStream(csvFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        CSVWriter csvWriter = new CSVWriter(outputStreamWriter);
        csvWriter.writeNext(new String[] {"number", "author", "title"});

        List<String[]> entries = new ArrayList<>();

        try {
            HttpEntity entity1 = response1.getEntity();
            InputStream content = entity1.getContent();

            String html = IOUtils.toString(content, String.valueOf(StandardCharsets.UTF_8));
            JSONArray objects = JSON.parseArray(html);

            for (int i = 0; i < n; i++) {
                JSONObject object = objects.getJSONObject(i);
                String title = (String) object.get("title");
                Object user = object.get("user");
                String author = (String) ((JSONObject) user).get("login");
                String number = String.valueOf(object.get("number"));

                String[] entry = {number, author, title};
                entries.add(entry);
            }
        } finally {
            response1.close();
            csvWriter.writeAll(entries);
            csvWriter.close();
        }
    }
}
