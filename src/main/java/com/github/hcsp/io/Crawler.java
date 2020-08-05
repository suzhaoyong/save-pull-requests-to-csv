package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<List<String>> records = getSpecifiedNumberPullRequests(repo, n);
        try (
                BufferedWriter writer = Files.newBufferedWriter(csvFile.toPath());
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("number", "author", "title"))
        ) {
            for (List<String> record : records) {
                csvPrinter.printRecord(record);
            }
            csvPrinter.flush();
        }
    }

    public static List<List<String>> getSpecifiedNumberPullRequests(String repo, int n) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=" + n)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }
        String responseBodyString = Objects.requireNonNull(response.body()).string();
        JSONArray jsonArray = JSON.parseArray(responseBodyString);
        response.close();
        List<List<String>> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonOne = jsonArray.getJSONObject(i);
            String number = String.valueOf(Integer.parseInt(jsonOne.getString("number")));
            String author = jsonOne.getJSONObject("user").getString("login");
            String title = jsonOne.getString("title");
            list.add(Arrays.asList(number, author, title));
        }
        return list;
    }
}
