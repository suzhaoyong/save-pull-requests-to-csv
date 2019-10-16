package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        OutputStream pullRequest = FileUtils.openOutputStream(csvFile);
        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls").get();
        Elements links = doc.select(".Box-row");
        String lineFirst = "number,author,title" + "\n";
        IOUtils.write(lineFirst, pullRequest, "UTF-8");
        if (n >= links.size()) {
            System.out.println("超过长度,请输入小于" + links.size() + "的长度");
        } else {
            for (Element link : links) {
                String number = link.attr("id").substring(6, 11);
                String author = link.select(".muted-link").first().text();
                String title = link.select(".link-gray-dark").text();
                String line = number + "," + author + "," + title + "\n";
                IOUtils.write(line, pullRequest, "UTF-8");
            }
        }
    }
}
