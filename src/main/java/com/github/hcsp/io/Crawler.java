package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
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
        GHRepository repository = GitHub.connectAnonymously().getRepository(repo);
        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));
        bufferedWriter.write("number"+","+"author"+","+"title"+"\r\n");

        for (int i = 1; i <= n; i++) {
            String line = pullRequests.get(i).getNumber() + ","
                    + pullRequests.get(i).getUser().getLogin() + ","
                    + pullRequests.get(i).getTitle() + "\r\n";
            bufferedWriter.write(line);
        }

        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
