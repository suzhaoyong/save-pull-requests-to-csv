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
    public static void main(String[] args) throws IOException {
        savePullRequestsToCSV("golang/go", 10, new File("pull.csv"));
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository repository = github.getRepository(repo);
        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);

        StringBuilder csvFileContent = new StringBuilder("number,author,title\n");
        for (int i = 0; i < 10; ++i) {
            int number = pullRequests.get(i).getNumber();
            String title = pullRequests.get(i).getTitle();
            String author = pullRequests.get(i).getUser().getLogin();

            String line = "" + number + "," + author + "," + title + "\n";
            csvFileContent.append(line);
        }
        Files.write(csvFile.toPath(), csvFileContent.toString().getBytes());
    }
}

