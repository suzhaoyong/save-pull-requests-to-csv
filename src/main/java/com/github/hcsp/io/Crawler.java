package com.github.hcsp.io;

import com.opencsv.CSVWriter;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository gitRepo = github.getRepository(repo);
        List<GHPullRequest> pullRequestList = gitRepo.getPullRequests(GHIssueState.OPEN);

        try (Writer writer = new FileWriter(csvFile)) {
            CSVWriter csvWriter = new CSVWriter(writer);
            csvWriter.writeNext(new String[]{"number", "author", "title"});
            for (int i = 0; i < n; i++) {
                csvWriter.writeNext(new String[]{String.valueOf(pullRequestList.get(i).getNumber()),
                        pullRequestList.get(i).getUser().getLogin(),
                        pullRequestList.get(i).getTitle()});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
