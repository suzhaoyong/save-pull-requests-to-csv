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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        // 创建一个HttpClient客户端
        HttpClient httpClient = HttpClients.createDefault();
        // 设置请求方式为：Get请求
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        // 执行Http一个请求，拿到一个响应
        HttpResponse httpResponse = httpClient.execute(httpGet);
        // 在响应中找到Body
        HttpEntity httpEntity = httpResponse.getEntity();
        // 在Body中拿到流
        InputStream is = httpEntity.getContent();
        // 将字符流转化为字符串
        String html = IOUtils.toString(is, "UTF-8");
        // 将字符串解析为html
        Document document = Jsoup.parse(html);
        // 筛选出CSS选择器中又.js-issue-row的标签
        Elements pullRequests = document.select(".js-issue-row");
        // 创建一个ArrayList集合
        List<String> contents = new ArrayList<>();
        // 向集合中添加"number,author,title"标题
        contents.add("number,author,title");
        // 循环添加
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
