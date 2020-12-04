package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.opencsv.CSVWriter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String url = "https://api.github.com/repos/" + repo + "/pulls";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String result = Objects.requireNonNull(response.body()).string();
            JSONArray array = JSON.parseArray(result);
            List<String[]> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String title = array.getJSONObject(i).get("title").toString();
                Object user = array.getJSONObject(i).get("user");
                String author = ((JSONObject) user).get("login").toString();
                String number = array.getJSONObject(i).get("number").toString();
                list.add(new String[]{number, author, title});
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
            CSVWriter csvWriter = new CSVWriter(bw);
            csvWriter.writeNext(new String[]{"number", "author", "title"});
            csvWriter.writeAll(list);
            csvWriter.close();
        }
    }
}
