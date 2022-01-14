package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository getrepo = github.getRepository(repo);
        List<GHPullRequest> prs = getrepo.getPullRequests(GHIssueState.OPEN);
        String csvcontent = "number,author,title" + "\n";
        for (int i = 0; i < n; i++) {
            int num = prs.get(i).getNumber();
            String user = prs.get(i).getUser().getLogin();
            String tit = prs.get(i).getTitle();
            String message = num + "," + user + "," + tit + "\n";
            csvcontent += message;
        }
        Files.write(csvFile.toPath(), csvcontent.getBytes(StandardCharsets.UTF_8));
    }
}
