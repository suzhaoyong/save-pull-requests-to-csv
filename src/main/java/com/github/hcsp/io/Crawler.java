package com.github.hcsp.io;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        //设置列名
        String header = "number" + ",author" + ",title\n";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(csvFile));
        bos.write(header.getBytes(Charset.defaultCharset()));
        //使用Jsoup解析html页面
        String uri = "https://github.com/" + repo + "/pulls/";
        Document document = Jsoup.connect(uri).get();
        //获取所有的pull request信息部分
        Elements elements = document.getElementsByClass("lh-condensed");
        for (int i = 0; i <= (elements.size() > 10 ? 10 : elements.size()); i++) {
            //获取number
            String number = elements.get(i).select(".opened-by").text().split("[# ]")[1];
            //获取author
            String author = elements.get(i).getElementsByAttributeValue("data-hovercard-type", "user").text();
            //获取标题title
            String title = elements.get(i).getElementsByAttributeValue("data-hovercard-type", "pull_request").text();
            String con = number + "," + author + "," + title + "\n";
            //写入数据
            bos.write(con.getBytes(Charset.defaultCharset()));
        }
        bos.close();
    }

}
