package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHubBuilder;

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
        List<GHPullRequest> pullRequests = new GitHubBuilder().build()
                .getRepository(repo)
                .queryPullRequests()
                .list()
                .asList();

//        System.out.println(pullRequests.asList());
        List<String> lines = new ArrayList<>();
        lines.add("number,author,title");
        for (int i = 0; i < n; i++) {
            GHPullRequest pullRequest = pullRequests.get(i);
            lines.add(pullRequest.getNumber() + ","
                    + pullRequest.getUser().getLogin() + ","
                    + pullRequest.getTitle());
        }

        FileUtils.writeLines(csvFile, lines);
    }
}
