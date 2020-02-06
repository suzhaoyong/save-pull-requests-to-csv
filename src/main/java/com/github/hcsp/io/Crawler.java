package com.github.hcsp.io;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static final OkHttpClient client = new OkHttpClient();
    private int nPull = 0;

    public Crawler(int nPull) {
        this.nPull = nPull;
    }


    Response getMethod(String url) throws IOException {
        Request request = new Request.Builder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
                .header("Content-Type", "application/json")
                .url(url)
                .build();
        return client.newCall(request).execute();
    }

    List<String> getPullRequestList(String url) throws IOException {
        List<String> ls = new ArrayList<>();
        Response respones = getMethod(url);
        JSONArray apiResponseArray = JSONArray.parseArray(Objects.requireNonNull(respones.body()).string());
        ls.add("number,author,title");
        for (Object pullRequest: apiResponseArray) {
            if (this.nPull-- == 0) {
                break;
            }
            JSONObject jsonObject  = (JSONObject) pullRequest;
            String id = jsonObject.getString("number");
            String author = jsonObject.getJSONObject("user").getString("login");
            String title = jsonObject.getString("title");
            ls.add(String.join(",", id, author, title));
        }
        return ls;
    }

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        Crawler crawler = new Crawler(n);
        String url = "https://api.github.com/repos/" + repo + "/pulls";
        List<String> lines = crawler.getPullRequestList(url);
        FileUtils.writeLines(csvFile, lines);
    }

    public static void main(String[] args) throws IOException {
        File file = new File("./test.csv");
        Crawler.savePullRequestsToCSV("gradle/gradle", 20, file);
    }
}
