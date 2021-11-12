package com.github.hcsp.io;

import org.kohsuke.github.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository getrepo = github.getRepository(repo);
        List<GHPullRequest> prs = getrepo.getPullRequests(GHIssueState.OPEN);
        //处理数据
        String csvcontent = "number,author,title" + "\n";
        for (int i = 0; i < n; i++) {
            //得到编号
            int num = prs.get(i).getNumber();
            //得到用户名
            String user = prs.get(i).getUser().getLogin();
            //得到标题
            String tit = prs.get(i).getTitle();
            //组合成一行信息
            String message = num + "," + user + "," + tit + "\n";
            csvcontent += message;
        }
        Files.write(csvFile.toPath(), csvcontent.getBytes(StandardCharsets.UTF_8));
    }
}
