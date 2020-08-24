package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    static class GitHubPullRequest {
        int number;
        String title;
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<GitHubPullRequest> gitHubPullRequestList = getFirstPageOfPullRequests(repo, n);
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(csvFile), CSVFormat.DEFAULT.withHeader("number", "author", "title"))) {
            for (GitHubPullRequest request : gitHubPullRequestList) {
                csvPrinter.printRecord(request.number, request.author, request.title);
            }
        }
    }

    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo, int n) {
        String url = String.format("https://api.github.com/repos/%s/pulls", repo);
        String response = HttpRequest.get(url).body();
        JSONArray dataArray = JSON.parseArray(response);

        return jsonDataToGitHubPullRequestList(dataArray, n);
    }

    public static List<GitHubPullRequest> jsonDataToGitHubPullRequestList(JSONArray jsonArray, int n) {
        List<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();

        if (n <= jsonArray.size()) {
            for (int i = 0; i < n; i++) {
                JSONObject dataItem = jsonArray.getJSONObject(i);
                int number = dataItem.getIntValue("number");
                String title = dataItem.getString("title");
                String author = dataItem.getJSONObject("user").getString("login");
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                gitHubPullRequestList.add(gitHubPullRequest);
            }
        }

        return gitHubPullRequestList;
    }

    public static void main(String[] args) {
        List<GitHubPullRequest> firstPageOfPullRequests = getFirstPageOfPullRequests("golang/go", 10);
        firstPageOfPullRequests.forEach(System.out::println);
    }
}
