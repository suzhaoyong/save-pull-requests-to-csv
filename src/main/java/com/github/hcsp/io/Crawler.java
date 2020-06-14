package com.github.hcsp.io;


import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindPirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        try {
            List<String> message = getMessage(repo, n);
            FileUtils.writeLines(csvFile, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMessage(String repo, int n) throws IOException {
        ArrayList<Element> elements = getElements(repo, n);
        List<String> message = new ArrayList<>();
        message.add("number,author,title");
        String line;
        for (int i = 0; i < n; i++) {
            String number = elements.get(i).text().split("#")[1].split(" ")[0];
            String author = elements.get(i).select(".muted-link").get(0).text();
            String title = elements.get(i).child(0).child(1).child(0).text();
            line = number + "," + author + "," + title;
            message.add(line);
        }
        return message;
    }

    public static ArrayList<Element> getElements(String repo, int n) throws IOException {
        return Jsoup.connect("https://github.com/" + repo + "/pulls").get().select(".js-issue-row");

    }
}
