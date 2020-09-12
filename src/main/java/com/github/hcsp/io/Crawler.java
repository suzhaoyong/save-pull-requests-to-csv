package com.github.hcsp.io;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
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
        Document document = Jsoup.connect("https://github.com/"+repo+"/pulls").get();
        ArrayList<Element> selecteddiv = document.select(".js-issue-row");
        List<String> list = new ArrayList<>();
        list.add("number,author,title");
        for (Element element : selecteddiv) {
            String title = element.child(0).child(1).child(0).text();
            String author = element.child(0).child(1).children().select(".text-small").select(".opened-by").select(".muted-link").text();
            String numberlong = element.child(0).child(1).children().select(".text-small").select(".opened-by").text();
            String number = numberlong.substring(1, 6);
            list.add(number + "," + author + "," + title);
        }
        Files.write(csvFile.toPath(), list, Charset.defaultCharset());

    }
}
