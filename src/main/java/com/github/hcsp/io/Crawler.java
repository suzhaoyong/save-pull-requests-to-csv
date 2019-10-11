package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<GHPullRequest> pulls = GitHub.connectAnonymously().getRepository(repo).getPullRequests(GHIssueState.OPEN);
        List<String> lines = pulls.stream().limit(n).map(Crawler::getLine).collect(Collectors.toList());
        List<String> contents = new ArrayList<>();
        contents.add("number,author,title");
        contents.addAll(lines);
        FileUtils.writeLines(csvFile, contents);
    }

    private static String getLine(GHPullRequest pull){
        int number = pull.getNumber();
        String author = null;
        String title = pull.getTitle();
        try {
            author = pull.getUser().getLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return number+","+author+","+title;
    }

    public static void main(String[] args) throws IOException {
        File tmp = File.createTempFile("csv", "");
        savePullRequestsToCSV("golang/go", 31, tmp);
    }
}
