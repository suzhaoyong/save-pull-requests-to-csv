package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Crawler {
  public static void main(String[] args) throws IOException {
    savePullRequestsToCSV("golang/go", 10, new File("x.csv"));
  }
  // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
  // number,author,title
  // 12345,blindpirate,这是一个标题
  // 12345,FrankFang,这是第二个标题
  public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
    GitHub github = GitHub.connectAnonymously();
    GHRepository ghRepository = github.getRepository(repo);
    List<GHPullRequest> pullRequests = ghRepository.getPullRequests(GHIssueState.OPEN);

    String csvContent = "number,author,title\n";

    for (int i = 0; i < n; i++) {
      GHPullRequest ghPullRequest = pullRequests.get(i);
      csvContent += getPullRequestInfo(ghPullRequest);
    }

    FileUtils.writeStringToFile(csvFile, csvContent);
  }

  private static String getPullRequestInfo(GHPullRequest pullRequest) throws IOException {
    int number = pullRequest.getNumber();
    String title = pullRequest.getTitle();
    String author = pullRequest.getUser().getLogin();
    return number + "," + author + "," + title + "\n";
  }
}
