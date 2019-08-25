package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<GHPullRequest> pullRequests = getPrFromGithub(repo);

        List<String> rows = new ArrayList<>();
        List<String> contents = pullRequests.stream().map(Crawler::getStringRowFromPr).collect(Collectors.toList());

        String title = "number,author,title";
        rows.add(title);

        for (int i = 0; i < n; i++) {
            rows.addAll(Collections.singleton(contents.get(i)));
        }

        Files.write(csvFile.toPath(), rows);
    }

    private static List<GHPullRequest> getPrFromGithub(String repo) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository repository = github.getRepository(repo);
        return repository.getPullRequests(GHIssueState.OPEN);
    }

    private static String getStringRowFromPr(GHPullRequest pr) {
        try {
            return pr.getNumber() + "," + pr.getUser().getLogin() + "," + pr.getTitle();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
