package com.github.hcsp.io;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        ArrayList<GitHubPullRequest> resultList = new ArrayList<>();
        ArrayList<String> outputList = new ArrayList<>();
        outputList.add("number,author,title");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?state=all&page=1");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            HttpEntity entity1 = response1.getEntity();
            String content = EntityUtils.toString(entity1, "UTF-8");
            content = "{ \"content\" :" + content + '}';
            JSONObject jsonContent = new JSONObject(content);
            JSONArray result = (JSONArray) jsonContent.get("content");
            for (int i = 0; i < n; i++) {
                JSONObject item = (JSONObject) result.get(i);
                String title = (String) item.get("title");
                int number = (int) item.get("number");
                JSONObject user = (JSONObject) item.get("user");
                String userName = (String) user.get("login");
                resultList.add(new GitHubPullRequest(number, title, userName));
                outputList.add(number + "," + userName + "," + title);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response1.close();
        }

        if (!resultList.isEmpty()) {
            FileUtils.writeLines(csvFile, outputList);
        }
    }
}
