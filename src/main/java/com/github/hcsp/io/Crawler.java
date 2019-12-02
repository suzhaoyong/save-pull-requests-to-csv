package com.github.hcsp.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.*;
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

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(csvFile));

        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("number", "author", "title");
        CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);

        for (int i = 0; i< pullRequests.size() && i < n; i++) {
            GHPullRequest pr = pullRequests.get(i);
            csvPrinter.printRecord(pr.getNumber(), pr.getUser().getLogin(), pr.getTitle());
        }
        csvPrinter.flush();
        csvPrinter.close();
    }
}
