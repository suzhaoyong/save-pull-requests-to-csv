package com.github.hcsp.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub gitHub = GitHub.connectAnonymously();
        GHRepository repository = gitHub.getRepository(repo);
        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.ALL);

        BufferedWriter writer = Files.newBufferedWriter(csvFile.toPath());
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("number", "author", "title"));

        List<List<String>> contents = IntStream.range(0, n)
                                                .mapToObj(index -> pullRequests.get(index))
                                                .map(Crawler::getLine)
                                                .collect(Collectors.toList());
        csvPrinter.printRecords(contents);
        //chiudere lo stream sempre!
        writer.close();
        csvPrinter.close();

    }

    public static List<String> getLine(GHPullRequest pullRequest) {
        try {
            return Arrays.asList(String.valueOf(pullRequest.getNumber()), pullRequest.getUser().getLogin(), pullRequest.getTitle());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
