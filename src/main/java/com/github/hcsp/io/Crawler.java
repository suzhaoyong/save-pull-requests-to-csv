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
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository gotRepo = github.getRepository(repo);
        List<GHPullRequest> gotPullRequests = gotRepo.getPullRequests(GHIssueState.ALL);
        writeCSVFile(buildCSVContent(gotPullRequests, n), csvFile);
    }

    public static StringBuilder buildCSVContent(List<GHPullRequest> gotPullRequests, int n) throws IOException {
        StringBuilder csvFileContent = new StringBuilder("number,author,title\n");
        String line;
        for (int i = 0; i < n; i++) {
            int number = gotPullRequests.get(i).getNumber();
            String user = gotPullRequests.get(i).getUser().getLogin();
            String title = gotPullRequests.get(i).getTitle();
            line = number + "," + user + "," + title + "\n";
            csvFileContent.append(line);
        }
        return csvFileContent;
    }

    private static void writeCSVFile(StringBuilder csvFileContent, File csvFile) throws IOException {
        FileUtils.writeStringToFile(csvFile, csvFileContent.toString(), "UTF-8");
    }

    public static void main(String[] args) throws IOException {
        savePullRequestsToCSV("golang/go", 15, new File ("pulls.csv"));
    }
}
