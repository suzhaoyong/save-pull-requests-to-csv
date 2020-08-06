package com.github.hcsp.io;

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        ArrayList<List> data = getPullRequestsData(repo);
        saveToCSV(data, n, csvFile);
    }

    private static ArrayList<List> getPullRequestsData(String repo) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        ArrayList<List> data = new ArrayList<>();
        try {
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            String Html = IOUtils.toString(is, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(Html);
            Elements issues = doc.select(".js-issue-row");
            for (Element element : issues) {
                String title = element.child(0).child(1).child(0).text();
                String author = element.child(0).child(1).select(".mt-1").get(0).child(0).select("a").text();
                String obtainNum = element.child(0).child(1).child(0).attr("href");
                String newNum = obtainNum.replace("/" + repo + "/pull/", "");
                ArrayList<String> newList = new ArrayList<>();
                Collections.addAll(newList, newNum, author, title);
                data.add(newList);
            }
        } finally {
            response.close();
        }
        return data;
    }

    private static void saveToCSV(ArrayList<List> data, int n, File csvFile) throws IOException {
        FileWriter output = new FileWriter(csvFile);
        output.write("number,author,title");
        for (int i = 0; i < n; i++) {
            output.write("\n");
            output.write(data.get(i).get(0) + "," + data.get(i).get(1) + "," + data.get(i).get(2));
        }
        output.close();
    }
}
