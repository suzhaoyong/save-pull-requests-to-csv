package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        Document document = Jsoup.parse(EntityUtils.toString(response.getEntity()));
        Elements pulls = document.getElementsByClass("js-issue-row");

        File file = new File(csvFile.getAbsolutePath());
        ArrayList<String> lines = new ArrayList<>();
        lines.add("number,author,title");

        for (Element pull : pulls) {
            String number = pull.attr("id").split("_")[1];
            String author = pull.getElementsByClass("muted-link").first().text();
            String title = pull.getElementById("issue_" + number + "_link").text();
            String line = number + "," + author + "," + title;
            lines.add(line);
            if (lines.size() == n + 1) {
                break;
            }
        }
        FileUtils.writeLines(file, lines);

    }
}
