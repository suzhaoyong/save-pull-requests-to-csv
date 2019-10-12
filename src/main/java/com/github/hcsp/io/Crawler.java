package com.github.hcsp.io;

import net.dongliu.requests.Requests;
import org.apache.commons.io.FileUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static final int PAGE_COUNT = 25;
    static final double PAGE_COUNT_DOUBLE = 25.0;

    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    public static List<GitHubPullRequest> getPageOfPullRequests(String repo, int page) throws IOException {
        String url = "https://github.com/" + repo + "/pulls" + "?page=" + page;
        Document doc = Jsoup.parse(Requests.get(url).send().readToText());
        ArrayList<Element> elements = doc.select(".js-issue-row");
        List<GitHubPullRequest> list = new ArrayList<>();
        for (Element element : elements) {
            String title = element.select("a[data-hovercard-type=\"pull_request\"]").text();
            String author = element.select("a[data-hovercard-type=\"user\"]").text();
            int number = Integer.parseInt(element.select(".opened-by").text().split(" ")[0].split("#")[1]);
            list.add(new GitHubPullRequest(number, title, author));
        }
        return list;
    }
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        if (n < 1) {
            throw new Error("设置数量好吗!");
        }
        ArrayList<GitHubPullRequest> pullRequests = new ArrayList<>();
        double pages = n / PAGE_COUNT_DOUBLE;
        for (int i = 0; i < pages; i++) {
            List<GitHubPullRequest> pullRequestOfPage = getPageOfPullRequests(repo, i + 1);
            pullRequests.addAll(pullRequestOfPage);
        }
        writeCSV(pullRequests, csvFile, n);
    }

    public static void writeCSV(List<GitHubPullRequest> pullRequests, File csvFile, int lineCount) throws IOException {
        ArrayList<String> lines = new ArrayList<>(lineCount + 1);
        lines.add("number,author,title");
        for (GitHubPullRequest item : pullRequests) {
            String line = item.number + "," + item.author + "," + item.title;
            lines.add(line);
        }
        FileUtils.writeLines(csvFile, lines);
    }

    public static void main(String[] args) throws IOException {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        File csvFile = new File(projectDir, "target/test.csv");
        Crawler.savePullRequestsToCSV("gradle/gradle", 100, csvFile);
    }
}
