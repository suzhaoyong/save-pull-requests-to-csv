package com.github.hcsp.io;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
//    public static void main(String[] args) throws IOException {
//        File tmp = File.createTempFile("csv", "");
//        savePullRequestsToCSV("golang/go",10,tmp);
//    }
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository repository = github.getRepository(repo);
        List<GHPullRequest> pullRequests = repository.getPullRequests(GHIssueState.OPEN);

        String csvFileCotent = "number,author,title" + "\n";
        List<String> liens = new ArrayList<>();
        liens.add(csvFileCotent);
        for (int i = 0; i < n; i++) {
            liens.add(getLine(pullRequests.get(i)));
//            csvFileCotent += getLine(pullRequests.get(i));
        }
//        Files.write(csvFile.toPath(),csvFileCotent.getBytes());
        Files.write(csvFile.toPath(), liens);
    }
    private static String getLine(GHPullRequest pullRequest){
            try {
//                return pullRequest.getNumber()+","+pullRequest.getTitle()+","+pullRequest.getUser().getLogin()+"\n";
                return pullRequest.getNumber()+","+pullRequest.getUser().getLogin()+","+pullRequest.getTitle()+"\n";
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }


