package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.opencsv.CSVWriter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {

        String url = "https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=10";
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        httpClient.executeMethod(getMethod);
        JSONArray responseList = JSON.parseArray(getMethod.getResponseBodyAsString());

        List<String[]> list = new ArrayList();
        list.add(new String[]{"number", "author", "title"});
        for (Object obj : responseList) {
            String number = ((JSONObject) obj).getString("number");
            String title = ((JSONObject) obj).getString("title");
            JSONObject userObj = ((JSONObject) obj).getJSONObject("user");
            String author = ((JSONObject) userObj).getString("login");
            String[] line = {number, author, title};
            list.add(line);
        }

        CSVWriter writer = new CSVWriter(new FileWriter(csvFile));
        writer.writeAll(list);
        writer.flush();
        writer.close();

    }

//    public static void main(String[] args) throws IOException {
//        File file = File.createTempFile("csv", "", new File(System.getProperty("basedir", System.getProperty("user.dir"))));
//        Crawler.savePullRequestsToCSV("golang/go", 10, file);
//    }
}
