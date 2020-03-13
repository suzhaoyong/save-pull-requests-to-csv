package com.github.hcsp.io;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的 GitHub 用户名
        String author;

        GitHubPullRequest(int number, String author, String title) {
            this.number = number;
            this.author = author;
            this.title = title;
        }

        @Override
        public String toString() {
            return number + "," + author + "," + title;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息

    public static List<GitHubPullRequest> getNumbersOfPullRequests(String repo, int n) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);


        ArrayList<GitHubPullRequest> list = new ArrayList<>();

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();

            // do something useful with the response body
            // and ensure it is fully consumed

            InputStream is = entity1.getContent();

            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            String theString = writer.toString();

            Gson gson = new Gson();

            OutBean[] beans = gson.fromJson(theString, OutBean[].class);

            for (int i = 0; i < n; i++) {
                list.add(new GitHubPullRequest(beans[i].getNumber(), beans[i].user.login, beans[i].getTitle()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response1.close();
        }

        return list;

    }


    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<GitHubPullRequest> list = getNumbersOfPullRequests(repo, n);
        for (GitHubPullRequest gitHubPullRequest : list) {
            System.out.println(gitHubPullRequest);
        }
        System.setOut(new PrintStream(csvFile));
        System.out.println("number,author,title");
        for (GitHubPullRequest gitHubPullRequest : list) {
            System.out.println(gitHubPullRequest);
        }
    }


}

class OutBean {
    int number;
    String title;
    InBean user;

    class InBean {
        String login;

        @Override
        public String toString() {
            return login;
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String toString() {
        return "OutBean{" +
                "number=" + number +
                ", title='" + title + '\'' +
                ", user=" + user +
                '}';
    }
}
