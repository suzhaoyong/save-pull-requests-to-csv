package com.github.hcsp.io;

import com.opencsv.CSVWriter;
import org.kohsuke.github.GHDirection;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestQueryBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private static List<String[]> prsList = new ArrayList<>();

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        PagedIterator<GHPullRequest> iterator = getPRIterator(repo);
        for (int i = 0; i < n; i++) {
            GHPullRequest pr = iterator.next();
            addPRToList(String.valueOf(pr.getNumber()), pr.getUser().getLogin(), pr.getTitle());
        }
        writeToCSV(prsList, csvFile);
    }


    private static PagedIterator<GHPullRequest> getPRIterator(String repo) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHPullRequestQueryBuilder ghPullRequestQueryBuilder = github.getRepository(repo).queryPullRequests();
        return ghPullRequestQueryBuilder.sort(GHPullRequestQueryBuilder.Sort.CREATED)
                .direction(GHDirection.DESC).list().iterator();
    }

    public static void addPRToList(String number, String author, String title) {
        String[] array = new String[]{
                number, author, title
        };
        prsList.add(array);
    }

    public static void writeToCSV(List<String[]> list, File csvFile) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(csvFile), ',');
        list.add(0, new String[]{"number", "author", "title"});
        writer.writeAll(list);
        writer.close();
    }
}
