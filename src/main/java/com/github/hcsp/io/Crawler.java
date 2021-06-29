package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String owner = repo.split("/")[0];
        String name = repo.split("/")[1];
        String url = "https://api.github.com";
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + "/repos/" + owner + "/" + name + "/pulls?per_page=" + n);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        System.out.println(httpResponse);
        HttpEntity entity = httpResponse.getEntity();
        InputStream inputStream = entity.getContent();
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            int b = inputStream.read();
            if (b == -1) {
                break;
            }
            stringBuilder.append((char) b);
        }

        List<Pull> pullList = JSON.parseArray(String.valueOf(stringBuilder), Pull.class);
        EntityUtils.consume(entity);
        FileWriter fileWriter = new FileWriter(csvFile);
        fileWriter.write("number,author,title");
        for (Pull pull : pullList) {
            fileWriter.write(System.lineSeparator());
            fileWriter.write(pull.getNumber() + "," + pull.getUser().getLogin() + "," + pull.getTitle());
        }
        fileWriter.close();
    }

    private static class Pull {
        private int number;
        private User user;
        private String title;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    private static class User {
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }

    public static void main(String[] args) throws IOException {
        savePullRequestsToCSV("gradle/gradle", 3, new File("xxx.txt"));
    }
}
