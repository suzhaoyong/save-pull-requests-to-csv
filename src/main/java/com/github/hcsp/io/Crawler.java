package com.github.hcsp.io;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(String.format("https://api.github.com/repos/%s/pulls?page=1&per_page=%d", repo, n));
        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
        Gson gson = new Gson();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(closeableHttpResponse.getEntity().getContent()));
        JsonObject[] jsonObjects = gson.fromJson(bufferedReader, JsonObject[].class);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));
        bufferedWriter.write("number," + "author," + "title");
        for (JsonObject jsonObject :
                jsonObjects) {
            bufferedWriter.newLine();
            int number = jsonObject.get("number").getAsInt();
            String author = jsonObject.getAsJsonObject("user").get("login").getAsString();
            String title = jsonObject.get("title").getAsString();
            String row = String.format("%d,%s,%s", number, author, title);
            bufferedWriter.write(row);
        }
        bufferedWriter.close();
    }
}

