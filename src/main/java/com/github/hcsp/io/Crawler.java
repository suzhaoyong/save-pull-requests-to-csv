package com.github.hcsp.io;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Crawler {

    private static final ArrayList<GitHubPullRequest> PULL_REQUESTS = new ArrayList<>();
    private static int numberLimitOfPR;

    /**
     * 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
     * number,author,title
     * 12345,blindpirate,这是一个标题
     * 12345,FrankFang,这是第二个标题
     *
     * @param repo    仓库名
     * @param n       前 n 个 Pull request
     * @param csvFile 用来存放 Pull request 信息的指定 csv 文件路径
     * @throws IOException 当写入出错时
     */
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {

        numberLimitOfPR = n;
        int page = 1;
        getSpecifiedPageOfPullRequestsAndStoreWithinLimit(repo, page);
        while (true) {
            if (PULL_REQUESTS.size() < n) {
                getSpecifiedPageOfPullRequestsAndStoreWithinLimit(repo, ++page);
            } else {
                break;
            }
        }
        FileWriter fw = new FileWriter(csvFile);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("number,author,title");
        bw.newLine();
        for (GitHubPullRequest pr : PULL_REQUESTS) {
            bw.write(pr.toString());
            bw.newLine();
        }
        bw.close();
        System.out.println(PULL_REQUESTS.size());
    }

    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }

        @Override
        public String toString() {
            return number + "," + author + "," + title;
        }
    }

    /**
     * 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，获取指定页的 Pull request 信息
     * 然后存够指定数量的 GitHubPullRequest 对象到 PULL_REQUESTS 中
     *
     * @param repo 仓库名
     * @param page 当前 Pull request 的页数
     * @throws IOException 当获取出错时
     */
    public static void getSpecifiedPageOfPullRequestsAndStoreWithinLimit(String repo, int page) throws IOException {
        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls" + "?page=" + page).get();
        ArrayList<Element> issues = doc.select(".js-issue-row");
        for (Element element : issues) {
            GitHubPullRequest pr = new GitHubPullRequest(
                    Integer.parseInt(element.attr("id").substring(6)),
                    element.select(".js-navigation-open").get(0).text(),
                    element.select(".muted-link").get(0).text()
            );
            if (PULL_REQUESTS.size() < numberLimitOfPR) {
                PULL_REQUESTS.add(pr);
            }
        }
    }

//    public static void main(String[] args) throws IOException {
//        savePullRequestsToCSV("golang/go", 40, new File("./temp.csv"));
//    }

}
