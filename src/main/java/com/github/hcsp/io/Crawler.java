package com.github.hcsp.io;


import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    public static String getURL(String repo, int page) {
        return "https://github.com/" + repo + "/pulls?page=" + page + "&q=is%3Apr+is%3Aopen";
    }

    public static String getLine(Element el) {
        int number = Integer.parseInt(el.attr("id").split("_")[1]);
        String title = el.getElementById(el.attr("id") + "_link").text();
        String author = el.getElementsByClass("opened-by").get(0).getElementsByTag("a").get(0).text();
        return number + "," + author + "," + title;
    }

    public static Elements getPulls(String repo, int page) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(getURL(repo, page));
        HttpResponse response = httpClient.execute(httpGet);

        HttpEntity entity1 = response.getEntity();

        return Jsoup.parse(EntityUtils.toString(entity1), "utf-8").getElementsByClass("js-issue-row");
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        int page = 1;

        List<String> list = new ArrayList<>();
        list.add("number,author,title");

        int i = 0;
        while (i < n) {
            Elements issueEles = getPulls(repo, page);
            for (int j = 0; j < issueEles.size(); j++) {
                list.add(getLine(issueEles.get(j)));
                if (i == n) {
                    break;
                }
                i++;
            }
        }

        FileUtils.writeLines(csvFile, list);
    }
}
