package com.github.hcsp.io;

import com.opencsv.CSVWriter;
import org.kohsuke.github.*;

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
        //获取指定的GitHubAPI内容
        GitHub gitHub = GitHub.connectAnonymously();
        GHPullRequestQueryBuilder ghPullRequestQueryBuilder = gitHub.getRepository(repo).queryPullRequests();
        PagedIterator<GHPullRequest> iterator = ghPullRequestQueryBuilder.sort(GHPullRequestQueryBuilder.Sort.CREATED)
                .direction(GHDirection.DESC).list().iterator();

        //将特定内容存入数组中
        List<String[]> prList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            GHPullRequest pr = iterator.next();
            String number = String.valueOf(pr.getNumber());
            String author = pr.getUser().getLogin();
            String title = pr.getTitle();
            String[] strings = new String[]{
                    number, author, title
            };
            prList.add(strings);
        }
        //将数组里的内容存入CSV文件中
        CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));
        prList.add(0, new String[]{"number", "author", "title"});
        csvWriter.writeAll(prList);
        csvWriter.close();

    }

}
