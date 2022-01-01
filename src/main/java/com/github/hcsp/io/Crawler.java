package com.github.hcsp.io;

import com.opencsv.CSVWriter;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import java.io.File;
import java.io.FileWriter;
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
        String[] csvFileContent = {"number", "author", "title"};
        List<String[]> data = new ArrayList<>();
        data.add(csvFileContent);
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(csvFile));
            for (int i = 0; i < n; i++) {
                String number = String.valueOf(pullRequests.get(i).getNumber());
                String title = pullRequests.get(i).getTitle();
                String author = pullRequests.get(i).getUser().getLogin();
                data.add(new String[]{number, author, title});
            }
            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
