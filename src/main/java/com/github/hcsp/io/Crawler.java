package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.opencsv.CSVWriter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {

    static class GitHubListPullRequestResponse {
        int number;
        String title;
        User user;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public GitHubListPullRequestResponse.User getUser() {
            return user;
        }

        public void setUser(GitHubListPullRequestResponse.User user) {
            this.user = user;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        static class User {
            String login;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }
        }
    }
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {

        // see https://developer.github.com/v3/pulls/#list-pull-requests
        final String RepoURL = "https://api.github.com/repos/" + repo + "/pulls";
        // 1. excute a http request and get response json string
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(RepoURL);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        String responseJson = EntityUtils.toString(response.getEntity(), "UTF-8");

        // 2. decode response json string
        List<GitHubListPullRequestResponse> gitHubListPullRequestResponses = JSON.parseArray(responseJson, GitHubListPullRequestResponse.class);

        // 3. map GitHubListPullRequestResponse=>List<String[]> [number,author,title]
        List<String[]> allLines = gitHubListPullRequestResponses
                .stream()
                .map(x -> new String[]{
                        String.valueOf(x.getNumber()),
                        x.getUser().getLogin(),
                        x.getTitle()})
                .collect(Collectors.toList());

        // 4. write to csv
        CSVWriter writer = new CSVWriter(new FileWriter(csvFile));
        writer.writeNext(new String[]{"number", "author", "title"});
        writer.writeAll(allLines);
        writer.close();
    }
}
