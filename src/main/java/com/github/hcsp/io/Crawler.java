package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        CloseableHttpClient document = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        try {
            CloseableHttpResponse httpResponse = document.execute(httpGet);
            InputStream content = httpResponse.getEntity().getContent();
            String body = IOUtils.toString(content, StandardCharsets.UTF_8);

            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile), ',', '"', '\\', "\n");
            csvWriter.writeNext(new String[]{"number", "author", "title"});
            csvWriter.flush();

            JSONArray jsonArray = JSON.parseArray(body);
            for (Object o : jsonArray) {
                JSONObject jsonObject = ((JSONObject) o);
                String number = jsonObject.getString("number");
                String author = jsonObject.getJSONObject("user").getString("login");
                String title = jsonObject.getString("title");
                String[] line = new String[]{number, author, title};
                csvWriter.writeNext(line);
                csvWriter.flush();
            }
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
