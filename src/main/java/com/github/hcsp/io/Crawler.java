package com.github.hcsp.io;
import org.kohsuke.github.*;
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
        GHRepository repository = github.getRepository(repo);
        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);

        String csvFileContent = "number,author,title\n";
        for (int i = 0; i < n; i++) {
            csvFileContent += getLine(pullRequests.get(i));
        }
        Files.write(csvFile.toPath(), csvFileContent.getBytes());
    }

    public static String getLine(GHPullRequest pullRequest) throws IOException {
        String line = String.format("%d,%s,%s\n", pullRequest.getNumber(), pullRequest.getUser().getLogin(), pullRequest.getTitle());
        return line;
    }

    public static void main(String[] args) throws IOException {
        savePullRequestsToCSV("gradle/gradle", 10, new File("pulls.csv"));
    }
}
