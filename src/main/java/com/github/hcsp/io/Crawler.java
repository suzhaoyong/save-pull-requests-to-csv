package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String html = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://GitHub.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                html = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            Document document = Jsoup.parse(html);
            ArrayList<Element> elements = document.select(".js-issue-row");
            FileUtils.writeLines(csvFile, Collections.singleton("number,author,title"), true);
            if ((elements.size() >= n) && (n >= 0)) {
                for (int i = 0; i < n; i++) {
                    String number = elements.get(i).attr("id").split("_")[1];
                    String title = elements.get(i).select("[data-hovercard-type=pull_request]").text();
                    String author = elements.get(i).select("[data-hovercard-type=user]").text();
                    StringBuffer pr = new StringBuffer().append(number).append(",").append(author).append(",").append(title);
                    FileUtils.writeLines(csvFile, Collections.singleton(pr), true);
                }
            }

        } finally {
            response.close();
        }
    }

}
