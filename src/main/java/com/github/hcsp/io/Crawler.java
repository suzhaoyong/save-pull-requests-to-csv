package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        class Pull {
            private Map<String, Object> user;
            int number;
            String author;
            String title;

            public Map<String, Object> getUser() {
                return user;
            }

            public void setUser(Map<String, Object> user) {
                this.user = user;
                this.author = (String) user.get("login");
            }

            public int getNumber() {
                return number;
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?per_page=" + n);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            List<Pull> pulls = JSON.parseArray(IOUtils.toString(response.getEntity().getContent()), Pull.class);
            ArrayList<String> pullLines = new ArrayList<>();
            pullLines.add("number,author,title");
            for (Pull pull : pulls) {
                pullLines.add(pull.number + "," + pull.author + "," + pull.title);
            }
            FileUtils.writeLines(csvFile, pullLines);
        } finally {
            response.close();
        }
    }
}
