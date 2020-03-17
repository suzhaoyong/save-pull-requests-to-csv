package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        int page = 1;
        List<String> saveL = new ArrayList<>();
        saveL.add("number,author,title");

        // 获取指定条数
        while (n > 0) {
            List<GitHubPRInfo> prL = getPRInfo(repo, page);
            if (!prL.isEmpty()) {
                for (int i = 0; i < Math.min(n, prL.size()); i++) {
                    GitHubPRInfo info = prL.get(i);
                    saveL.add(info.toString());
                }
                n -= prL.size();
                page++;
            }
        }
        FileUtils.writeLines(csvFile, "UTF-8", saveL);
    }

    public static void main(String[] args) throws IOException {
        File f = new File("./target/prInfo.csv");
        savePullRequestsToCSV("golang/go", 40, f);
    }

    private static List<GitHubPRInfo> getPRInfo(String repo, int page) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?state=all&page=" + page);
        httpGet.setHeader("user-agent", "Mozilla");
        CloseableHttpResponse getRsp = httpclient.execute(httpGet);
        HttpEntity getRspEntity = getRsp.getEntity();
        String result = IOUtils.toString(getRspEntity.getContent(), StandardCharsets.UTF_8);

        return JSON.parseObject(result, new TypeReference<List<GitHubPRInfo>>() {}); // 泛型反序列化
    }

    static class GitHubPRInfo {
        private long number;
        private String author;
        private String title;

        public void setNumber(long number) {
            this.number = number;
        }

        public void setUser(Map<String, Object> user) {
            if (user != null && user.get("login") != null) {
                this.author = user.get("login").toString();
            }
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return number + "," + author + "," + title;
        }
    }
}
