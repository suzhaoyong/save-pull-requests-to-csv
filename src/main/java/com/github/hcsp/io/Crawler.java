package com.github.hcsp.io;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        InputStream is = httpEntity.getContent();
        String html = IOUtils.toString(is, StandardCharsets.UTF_8);
        Document document = Jsoup.parse(html);
        Elements pullRequests = document.select(".js-issue-row");
        List<String> contents = new ArrayList<>();
        contents.add("number,author,title");
        for (int i = 0; i < n; i++) {
            Element element = pullRequests.get(i);
            if (element == null) {
                break;
            }
            int number = Integer.parseInt(element.select(".opened-by").text().substring(1, 6));
            String title = element.select(".js-navigation-open").text();
            String author = element.child(0).child(1).select(".mt-1").select(".opened-by").select(".Link--muted").text();
            String content = number + "," + author + "," + title;
            System.out.println(content);
            contents.add(content);
        }
        Files.write(csvFile.toPath(), contents, Charset.defaultCharset());
    }
}
