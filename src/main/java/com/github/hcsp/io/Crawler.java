package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    static class GithubResponse {
        String number;
        String title;
        User user;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
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


    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        final String url = "https://api.github.com/repos/" + repo + "/pulls";
        List<GithubResponse> githubResponses = sendHttpAndGetResponse(url);
        FileUtils.writeStringToFile(csvFile, "number,author,title" + System.getProperty("line.separator"), Charset.defaultCharset());
        for (int i = 0; i < n; i++) {
            String result = githubResponses.get(i).getNumber() + "," + githubResponses.get(i).getUser().login + "," + githubResponses.get(i).getTitle();
            FileUtils.writeStringToFile(csvFile, result + System.getProperty("line.separator"), Charset.defaultCharset(), true);
        }
    }

    public static List<GithubResponse> sendHttpAndGetResponse(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        String response = EntityUtils.toString(httpEntity, Charset.defaultCharset());
        List<GithubResponse> githubResponses = JSON.parseArray(response, GithubResponse.class);
        httpClient.close();
        return githubResponses;
    }

    public static void main(String[] args) throws IOException {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        File testFile = new File(projectDir, "target/test.csv");
        savePullRequestsToCSV("vuejs/vue", 2, testFile);
    }
}
