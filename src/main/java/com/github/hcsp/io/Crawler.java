package com.github.hcsp.io;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import org.kohsuke.github.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {

        List<String[]> entries = new ArrayList<>();
        entries.add(new String[]{"number", "author", "title"}); //第一列

        GHRepository repository =
                GitHub.connectAnonymously().getRepository(repo);
        GHPullRequestQueryBuilder ghPRBuilder = repository.queryPullRequests();
        List<GHPullRequest> ghPullRequests = ghPRBuilder.list().asList();
        for ( int i = 0; i < n; i++ ){
            GHPullRequest pull = ghPullRequests.get(i);
            String number = String.valueOf( pull.getNumber() );
            String author = pull.getUser().getLogin();
            String title = pull.getTitle();
            String[] entry = {number, author, title};
            entries.add(entry);
        }

        OutputStreamWriter is = new OutputStreamWriter(new FileOutputStream(csvFile), "utf-8");
        ICSVWriter build = new CSVWriterBuilder(is).build();
        build.writeAll(entries, false);
        build.flushQuietly();

    }
}
