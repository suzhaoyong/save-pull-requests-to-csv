package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        try {
            GHRepository gp = GitHub.connectAnonymously().getRepository(repo);
            List<GHPullRequest> prs = gp.getPullRequests(GHIssueState.OPEN);

            List<String> allLines = new ArrayList<>();
            allLines.add(String.join(",", "number", "author", "title"));
            for (int i = 0; i < Math.min(n, prs.size()); i++) {
                GHPullRequest pr = prs.get(i);
                allLines.add(String.join(",", "" + pr.getNumber(), "\"" + pr.getUser().getLogin() + "\"", "\"" + pr.getTitle() + "\""));
            }
            Files.write(csvFile.toPath(), allLines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
