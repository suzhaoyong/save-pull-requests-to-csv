package com.github.hcsp.io;

import com.opencsv.CSVWriter;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;

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
        List<GHPullRequest> pulls = getRepoOpenPullRequests(repo, n);

        FileWriter outputfile = new FileWriter(csvFile);
        CSVWriter writer = new CSVWriter(outputfile);

        String[] header = {"number", "author", "title"};
        writer.writeNext(header);
        for (int i = 0; i < n; i++) {
            writer.writeNext(mapPullToStringArray(pulls.get(i)));
        }
        writer.close();
    }

    private static String[] mapPullToStringArray(GHPullRequest pull) throws IOException {
        String[] item = new String[3];
        item[0] = pull.getNumber() + "";
        item[1] = pull.getUser().getLogin();
        item[2] = pull.getTitle();
        return item;
    }

    private static List<GHPullRequest> getRepoOpenPullRequests(String repo, int count) throws IOException {
        int total = getRepoOpenPullRequestCount(repo);
        if (total < count) {
            throw new Error("你要获取仓库" + repo + "的" + count + "条pr 大于实际的open个数" + total);
        }
        return GitHub.connectAnonymously()
                .getRepository(repo)
                .getPullRequests(GHIssueState.OPEN);
    }

    private static int getRepoOpenPullRequestCount(String repo) throws IOException {
        return GitHub.connectAnonymously()
                .getRepository(repo)
                .getOpenIssueCount();
    }

    public static void main(String[] args) throws IOException {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        File testFile = new File(projectDir, "target/test.csv");
        savePullRequestsToCSV("golang/go", 30, testFile);
    }
}
