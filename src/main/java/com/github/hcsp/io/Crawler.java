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
