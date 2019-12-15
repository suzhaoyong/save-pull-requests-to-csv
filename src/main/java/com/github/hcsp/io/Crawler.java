package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws Exception{
        boolean repoIsEmpty = repo==null || "".equals(repo);
        boolean csvFileError = csvFile == null || !csvFile.isFile();
        if (n<0 || repoIsEmpty || csvFileError) {
            return;
        }
        String url = "https://api.github.com/repos/"+ repo +"/pulls";
        String content = getUrlContent(url);
        List<GitHubPullRequest> requests = parse(content);

        List<String> lines = new LinkedList<>();
        lines.add("number,author,title");
        for (int i=0; i<Math.min(requests.size(), n); i++){
            GitHubPullRequest request = requests.get(i);
            String line = String.join(",", new String[]{request.number+"", request.author, request.title});
            lines.add(line);
        }

        Files.write(csvFile.toPath(), lines);
    }
    private static class Group{
        private String title;
        private int number;
        public User user;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }
    private static class User{
        private String login;

        public void setLogin(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    static List<GitHubPullRequest> parse(String githubApiJson){
        List<GitHubPullRequest> requests = new LinkedList<>();
        List<Group> list = JSON.parseArray(githubApiJson, Group.class);
        for (Group group: list){
            int number = group.number;
            String author = group.user.login;
            String title = group.title;
            requests.add( new GitHubPullRequest(number, title, author) );
        }

        return requests;
    }

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

        @Override
        public String toString() {
            return "{number="+number+", title="+title+", author="+author+"}";
        }
    }

    private static String getUrlContent(String url)throws Exception{
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
        StringBuilder result = new StringBuilder();
        try{
            HttpEntity httpEntity = response.getEntity();
            InputStream is = httpEntity.getContent();
            byte[] bytes = new byte[1024];
            while (true) {
                int b = is.read(bytes);
                if (b==-1){
                    break;
                }
                result.append(new String(bytes, 0, b));
            }
            is.close();
        }finally {
            response.close();
            return result.toString();
        }

    }
}
