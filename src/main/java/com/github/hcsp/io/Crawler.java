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

        List<GHPullRequest> ghPullRequests = accessAndGetPullRequests(repo);

        String content = "number,author,title\n";
        for (int i = 0; i < n; i++) {
            String title = ghPullRequests.get(i).getTitle();
            int number = ghPullRequests.get(i).getNumber();
            String author = ghPullRequests.get(i).getUser().getLogin();

            String line = number + "," + author + "," + title + "\n";
            content += line;
        }

        Files.write(csvFile.toPath(), content.getBytes());
    }

    private static List<GHPullRequest> accessAndGetPullRequests(String repo) throws IOException {
        // 使用匿名链接，访问到 GitHub
        GitHub github = GitHub.connectAnonymously();
        // 获取对应的仓库信息
        GHRepository repository = github.getRepository(repo);
        // 返会所有状态为 OPEN 的 PR 信息
        return repository.getPullRequests(GHIssueState.OPEN);
    }
}
