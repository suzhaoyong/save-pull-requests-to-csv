package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub gitHub = GitHub.connectAnonymously();
        GHRepository repository = gitHub.getRepository(repo);
        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);
        List<String> list = new ArrayList<>();
        list.add("number,author,title");

        for (int i = 0; i < n; i++) {
            int number = pullRequests.get(i).getNumber();
            String author = pullRequests.get(i).getUser().getLogin();
            String title = pullRequests.get(i).getTitle();
            list.add(number + "," +  author + "," + title);

        }
        FileUtils.writeLines(csvFile, list);
    }
}
