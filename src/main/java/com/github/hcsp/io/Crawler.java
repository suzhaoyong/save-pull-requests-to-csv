package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    static OkHttpClient client = new OkHttpClient();
    static final String[] HEADERS = {"number", "author", "title"};

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        createCSVFile(getPullRequest(repo, n), csvFile);
    }

    public static void createCSVFile(List<PullRequest> list, File csvFile) throws IOException {
        FileWriter out = new FileWriter(csvFile);
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS));
        list.forEach(pr -> {
                    try {
                        printer.printRecord(pr.getNumber(), pr.getUser().getLogin(), pr.getTitle());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        out.flush();
        printer.close(true);
        out.close();

    }

    static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .addHeader("User-Agent", "PostmanRuntime/7.26.8")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    static List<PullRequest> getPullRequest(String repo, int n) throws IOException {
        String url = "https://api.github.com/repos/" + repo + "/pulls?state=open";
        List<PullRequest> list = JSON.parseArray(get(url), PullRequest.class);
        return list.stream()
                .sorted((pr1, pr2) -> -1 * pr1.getCreatedAt().compareTo(pr2.getCreatedAt()))
                .limit(n)
                .collect(Collectors.toList());
    }

    private static class PullRequest {
        private int number;
        private String title;
        private Date createdAt;
        private User user;

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

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
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
}
