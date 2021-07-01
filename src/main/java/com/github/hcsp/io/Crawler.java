package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository repository = github.getRepository(repo);
        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);

        List<String> list = new java.util.ArrayList<>(Collections.singletonList("number,author,title"));

        for (GHPullRequest pullRequest : pullRequests) {
            list.add(getLine(pullRequest));
            if (list.size() >= n + 1) {
                break;
            }
        }

        FileWriter fileWriter = new FileWriter(csvFile);
        fileWriter.write(list.stream().collect(Collectors.joining(System.lineSeparator())));
        fileWriter.close();

    }

    public static String getLine(GHPullRequest pullRequest) {
        try {
            return pullRequest.getNumber() + "," + pullRequest.getUser().getLogin() + "," + pullRequest.getTitle();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        savePullRequestsToCSV("gradle/gradle", 3, new File("pr.csv"));
    }
}
