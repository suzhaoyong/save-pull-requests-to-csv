package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<GitHubPullRequest> gitHubPullRequests = getFirstPageOfPullRequests(repo, n);
        JSONArray gitHubPullRequestsJSONArray = new JSONArray();
        for (int i = 0; i < gitHubPullRequests.size(); i++) {
            JSONObject job = new JSONObject();
            job.put("number", gitHubPullRequests.get(i).getNumber());
            job.put("author", gitHubPullRequests.get(i).getAuthor());
            job.put("title", gitHubPullRequests.get(i).getTitle());

            gitHubPullRequestsJSONArray.put(job);
        }
        String csv = CDL.toString(gitHubPullRequestsJSONArray);
        FileUtils.writeStringToFile(csvFile, csv, Charset.defaultCharset());
    }

    public static void main(String[] args) throws IOException {
        File tmp = File.createTempFile("csv", "");
        savePullRequestsToCSV("golang/go", 10, tmp);
    }

    static class GitHubPullRequest implements Serializable {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的 GitHub 用户名
        String author;

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

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo, int n) throws IOException {
        ArrayList<GitHubPullRequest> gitHubPullRequests = new ArrayList<GitHubPullRequest>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com" + "/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            HttpEntity entity = response.getEntity();

            String requestString = IOUtils.toString(entity.getContent(), "utf-8");
            JSONArray pullRequestArray = new JSONArray(requestString);

            for (int i = 0; i < n; i++) {
                JSONObject jsonObject = pullRequestArray.getJSONObject(i);
                int number = (int) jsonObject.get("number");
                String title = (String) jsonObject.get("title");
                JSONObject user = (JSONObject) jsonObject.get("user");
                String author = (String) user.get("login");
                gitHubPullRequests.add(new GitHubPullRequest(number, title, author));
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return gitHubPullRequests;
    }
}
