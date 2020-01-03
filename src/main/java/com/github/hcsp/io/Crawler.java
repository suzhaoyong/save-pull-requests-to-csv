package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
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
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        checkIsValidFile(csvFile);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile))) {
            List<GitHubPullRequest> requests = getNPullRequests(repo, n);
            bufferedWriter.write(String.join(",", "number", "author", "title"));
            bufferedWriter.write('\n');
            for (GitHubPullRequest request : requests) {
                bufferedWriter.write(String.join(",", String.valueOf(request.number), request.author, request.title));
                bufferedWriter.write('\n');
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Fail to fetch %d pull requests from %s", n, repo));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Fail to write to file " + csvFile.getAbsolutePath());
        }
    }

    private static class GitHubPullRequest {
        private String author;
        private String title;
        private int number;

        private GitHubPullRequest(int number, String author, String title) {
            this.author = author;
            this.title = title;
            this.number = number;
        }
    }

    private static void checkIsValidFile(File csvFile){
        if (!csvFile.exists()) {
            throw new IllegalArgumentException("File " + csvFile.getAbsolutePath() + " does not exist.");
        }

        if (!csvFile.canWrite()) {
            throw new IllegalArgumentException("Cannot write to file " + csvFile.getAbsolutePath());
        }

//        if (!csvFile.getName().endsWith(".csv")){
//            throw new IllegalArgumentException("File " + csvFile.getAbsolutePath() + " is not a csv file.");
//        }
    }

    private static List<GitHubPullRequest> getNPullRequests(String repo, int n) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(String.format("https://api.github.com/repos/%s/pulls?state=open&per_page=%d", repo, n));
        List<GitHubPullRequest> result = new ArrayList<>();
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        JSONArray arr = JSON.parseArray(IOUtils.toString(is, "UTF-8"));

        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String author = obj.getJSONObject("user").getString("login");
            String title = obj.getString("title");
            int number = obj.getInteger("number");
            result.add(new GitHubPullRequest(number, author, title));
        }

        return result;
    }

    public static void main(String[] args) {
//        getNPullRequests("gradle/gradle", 3);
//        System.out.println(String.join(",", "A", "B"));
//        File file = new File("/Users/xipu/Desktop/test.java");
    }
}
