package com.github.hcsp.io;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = new GitHubBuilder().build();
        PagedIterable<GHPullRequest> pulls = github.getRepository(repo).queryPullRequests().list();
        BufferedWriter bf = new BufferedWriter(new FileWriter(csvFile));
        bf.write("number,author,title");
        bf.newLine();
        int count = 0;
        for (Iterator<GHPullRequest> pull = pulls.iterator(); n > count && pull.hasNext();) {
            bf.write(Crawler.getPullBaseInfo(pull.next()));
            bf.newLine();
            count++;
        }
        bf.close();
    }

    private static String getPullBaseInfo(GHPullRequest pull) {
        try {
            return pull.getNumber() + "," + pull.getUser().getLogin() + "," + pull.getTitle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
