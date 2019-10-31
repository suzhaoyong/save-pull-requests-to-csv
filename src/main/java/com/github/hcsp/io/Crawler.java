package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void main(String[] args) throws IOException {
        savePullRequestsToCSV("gradle/gradle", 15, new File("/Users/Jason/Desktop/appTest/test.csv"));
    }

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<GitHubPullRequest> list = getPullRequests(repo, n).subList(0, n);
        List<String> newList = new ArrayList<>();
        newList.add("number,author,title");
        for (GitHubPullRequest element :
                list) {
            newList.add(element.number+","+element.user.login+","+element.title);
        }
        FileUtils.writeLines(csvFile, newList);
    }

    public static List<GitHubPullRequest> getPullRequests(String repoName, int n) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.github.com/repos/" + repoName + "/pulls?page=1&per_page=20");
        CloseableHttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        // return it as a String
        String result = EntityUtils.toString(entity);
        return JSON.parseObject(result, new TypeReference<List<GitHubPullRequest>>() {
        });
    }

    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        User user;

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

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", title='" + title + '\'' +
                    ", user=" + user +
                    '}';
        }
    }

    static class User {
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        @Override
        public String toString() {
            return login;
        }
    }
}
