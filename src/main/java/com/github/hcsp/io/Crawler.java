package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository ghRepository = github.getRepository(repo);
        List<GHPullRequest> pullRequests = ghRepository.getPullRequests(GHIssueState.OPEN);
        StringBuilder fileContent = new StringBuilder("number,author,title\n");
        for (int i = 0; i < n; i++) {
            int number = pullRequests.get(i).getNumber();
            String author = pullRequests.get(i).getUser().getLogin();
            String title = pullRequests.get(i).getTitle();
            fileContent.append(number).append(",").append(author).append(",").append(title).append("\n");
        }
        Files.write(csvFile.toPath(), fileContent.toString().getBytes());
    }
}
