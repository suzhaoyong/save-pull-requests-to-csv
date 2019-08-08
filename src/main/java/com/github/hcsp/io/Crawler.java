package com.github.hcsp.io;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题


    public static synchronized void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        Document doc;
        try {
            doc = Jsoup.connect(repo).get();
        }catch(IllegalArgumentException o){
            repo = "https://github.com/" + repo + "/pulls";
            doc = Jsoup.connect(repo).get();
        }
        Elements elements = doc.select(".js-issue-row");
        boolean flag = true;
        for (Element e : elements) {
            String[] number_temp = e.child(0).child(1).select(".text-small").text().split(" ");
            int number = Integer.valueOf(number_temp[0].substring(1));
            String title = e.child(0).child(1).child(0).text();
            String author = e.child(0).child(1).select(".muted-link").first().text();
            save(number, author, title, csvFile);
            if (--n == 0) {
                flag = false;
                break;
            }
        }
        if (flag) {
            repo = "https://github.com" + doc.select("[rel=next]").first().attr("href");
            savePullRequestsToCSV(repo, n, csvFile);
        }
    }

    private static void save(int number, String author, String title, File csvFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true));
        if (csvFile.length() == 0){
            writer.write("number" + "," + "author" + "," + "title" + "\n");
        }
        writer.write(number + "," + author + "," + title + "\n");
        writer.close();
    }
}

