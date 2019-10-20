package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String response = HttpRequest.get("https://api.github.com/repos/" + repo + "/pulls").body();
        JSONArray pulls = JSON.parseArray(response);
        List<String> list = new ArrayList<>();
        int length = Math.min(pulls.size(), n);
        list.add("number,author,title");
        for (int i = 0; i < length; i++) {
            JSONObject pr = pulls.getJSONObject(i);
            Integer number = pr.getInteger("number");
            String title = pr.getString("title");
            String author = pr.getJSONObject("user").getString("login");
            list.add(number + "," + author + "," + title);
        }
        Files.write(csvFile.toPath(), list);
    }
}
