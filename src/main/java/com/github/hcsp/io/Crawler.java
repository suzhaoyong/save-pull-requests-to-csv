package com.github.hcsp.io;

import net.dongliu.requests.Requests;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    static int getNumberOfOpenInfo(String openedBy) {
        String numberStr = openedBy.split("opened")[0];
        return Integer.valueOf(numberStr.split("#")[1].trim());
    }

    public static List<GitHubPullRequest> getPullRequestOfPage(String repo, int page) {
        String url = "https://github.com/" + repo + "/pulls" + "?page=" + page;

        Document doc = Jsoup.parse(Requests.get(url).send().readToText());
        Elements issueElements = doc.select(".js-issue-row");

        ArrayList<GitHubPullRequest> pullRequestList = new ArrayList<>();

        for (Element issue : issueElements) {
            String title = issue.select("a[data-hovercard-type=\"pull_request\"]").text();
            String author = issue.select("a[data-hovercard-type=\"user\"]").text();

            int number = getNumberOfOpenInfo(issue.select(".opened-by").text());

            pullRequestList.add(new GitHubPullRequest(number, title, author));
        }

        return pullRequestList;
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        if (n < 1) {
            throw new Error("Required: n > 0 !");
        }

        ArrayList<GitHubPullRequest> pullRequests = new ArrayList<>();

        double pages = n / PAGE_COUNT_DOUBLE;
        int lastPageCount = n % PAGE_COUNT;

        for (int i = 0; i < pages; i++) {
            List<GitHubPullRequest> pullRequestOfPage = getPullRequestOfPage(repo, i + 1);

            // 处理最后一页
            if (i == Math.floor(pages)) {
                pullRequests.addAll(pullRequestOfPage.subList(0, lastPageCount));
            } else {
                pullRequests.addAll(pullRequestOfPage);
            }
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
}
