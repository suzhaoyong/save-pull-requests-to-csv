package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Crawler {
    private static final String GITHUB_API = "https://api.github.com";
    private static final String CSV_FILE_TITLE = "number,author,title" + System.lineSeparator();

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        try {
            List<GithubPullRequest> pulls = getRepoPulls(repo, n);
            savePullsToCsv(csvFile, pulls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<GithubPullRequest> getRepoPulls(String repo, int n) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(GITHUB_API + "/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        List<GithubPullRequest> pulls;
        try {
            HttpEntity entity = response.getEntity();
            pulls = JSON.parseArray(IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8),
                    GithubPullRequest.class);
            return pulls.subList(0, n);
        } finally {
            response.close();
        }
    }

    public static void savePullsToCsv(File csvFile, List<GithubPullRequest> pulls) throws IOException {
        FileUtils.writeStringToFile(csvFile, CSV_FILE_TITLE, StandardCharsets.UTF_8);
        FileUtils.writeLines(csvFile, pulls, true);
    }

    private static class GithubPullRequest {
        private int number;
        private String title;
        private GithubUser user;

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

        public GithubUser getUser() {
            return user;
        }

        public void setUser(GithubUser user) {
            this.user = user;
        }

        private static class GithubUser {
            private String login;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }
        }

        @Override
        public String toString() {
            return number + "," + user.login + "," + title;
        }
    }
}
