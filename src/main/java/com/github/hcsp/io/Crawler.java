package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        BufferedWriter bf = new BufferedWriter(new FileWriter(csvFile));
        bf.write("number,author,title");
        List<GHPullRequest> pullRequests = GitHub.connectAnonymously().getRepository(repo).getPullRequests(GHIssueState.CLOSED);
        for (int i = 0; i < n; i++) {
            bf.newLine();
            bf.write(pullRequests.get(i).getNumber() + "," + pullRequests.get(i).getUser().getLogin() + "," + pullRequests.get(i).getTitle());
        }
        bf.flush();
        bf.close();
    }
}
