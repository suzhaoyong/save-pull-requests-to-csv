package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        //创建一个客户端
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //使用get方法获取网页信息
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        //获得响应并打印响应状态
        CloseableHttpResponse response = httpclient.execute(httpGet);
        System.out.println(response.getStatusLine());
        //获取响应体
        HttpEntity entity1 = response.getEntity();
        //将响应体的内容解析成输入流
        InputStream is = entity1.getContent();
        //将输入流转化成字符串
        String html = IOUtils.toString(is, StandardCharsets.UTF_8);
        //将字符串解析成html文件
        Document doc = Jsoup.parse(html);
        //筛选出CSS选择器中又.js-issue-row的标签
        ArrayList<Element> pulls = doc.select(".js-issue-row");

        List<String> list = new ArrayList<>();
        list.add("number,author,title");

        for (int i = 0; i < n; ++i) {
            Element element = pulls.get(i);
            if (element == null) {
                break;
            }
            int size = element.child(0).child(1).childrenSize();
            int number = Integer.parseInt(element.child(0).child(1).child(size - 1).child(0).text().substring(1, 6));
            String title = element.select(".js-navigation-open").text();
            String author = element.child(0).child(1).child(size - 1).child(0).child(1).text();
            String str = Integer.toString(number) + ',' + author + ',' + title;
            System.out.println(str);
            list.add(str);
        }
        FileUtils.writeLines(csvFile, list);
    }
}
