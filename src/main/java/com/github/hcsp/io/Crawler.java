package com.github.hcsp.io;

import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        // 初始化数组
        String path = csvFile.getPath();
        List<String> list = getPullRequestList(repo, n);

        File file = new File(path);
        FileWriter fw = new FileWriter(file); //创建FileWriter对象

        int i = 0;
//        FileUtils.writeLines(csvFile, list);
        while (i <= n){
            fw.write(String.valueOf(list.get(i))); //向文件写入数据
            i++;
        }
        fw.close();
    }

    public static List<String> getPullRequestList(String repo, int n) throws IOException {
        String url = "https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=" + n;
        List<String> list = new ArrayList();
        list.add("number,author,title\n");

        String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
        JSONArray jsonArray = JSONArray.parseArray(json);

        for (int i = 0; i < n; i++) {
            String number = jsonArray.getJSONObject(i).getString("number");
            String title = jsonArray.getJSONObject(i).getString("title");
            String author = jsonArray.getJSONObject(i).getJSONObject("user").getString("login");
            list.add(String.join(",", number, author, title+"\n"));
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("./test.csv");
        Crawler.savePullRequestsToCSV("gradle/gradle", 5, file);
    }
}
