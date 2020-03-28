package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        readPullRequestsFrom(repo, n, csvFile);
    }

    private static void readPullRequestsFrom(String repo, int limits, File csvFile) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "https://api.github.com/repos/" + repo +"/pulls";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String text = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            List<GHPullRequest> pullRequests = JSON.parseArray(text, GHPullRequest.class);

            String title = "number,author,title";
            bw.append(title).append("\n");
            int count = 0;
            for (GHPullRequest pr: pullRequests) {
                if (limits > 0 && count >= limits) {
                    break;
                }
                bw.append(pr.getNumber() + ",");
                bw.append(pr.getAuthor() + ",");
                bw.append(pr.getTitle());
                bw.append("\n");
                count++;
            }
        } finally {
            response.close();
            bw.close();
        }
    }

    private static class GHPullRequest {
        private int number;
        private String title;
        private GHUser user;

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
            return user.login;
        }

        public void setUser(GHUser user) {
            this.user = user;
        }

        private class GHUser {
            private String login;
            private String url;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
