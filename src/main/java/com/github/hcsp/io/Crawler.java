package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
public class Crawler {
    static class GithubPullRequest {
        int number;
        String author;
        String title;

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
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "{" +
                    "number=" + number +
                    ", author='" + author + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }

        GithubPullRequest(int number, String author, String title) {
            this.number = number;
            this.author = author;
            this.title = title;
        }
    }
    static class PullRequests {
        private int number;
        private int id;
        private String title;
        private User user;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        @Override
        public String toString() {
            return "PullRequests{" +
                    "number=" + number +
                    ", id=" + id +
                    ", title='" + title + '\'' +
                    ", user=" + user +
                    '}';
        }
    }

    static class User {
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        @Override
        public String toString() {
            return "User{" +
                    "login='" + login + '\'' +
                    '}';
        }
    }

    public static void savePullRequestsToCSV(String repo, int n, File file) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        List<PullRequests> jsonArray;
        List<GithubPullRequest> githubPullRequests = new ArrayList<>();

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity.getContent();
            String responsBodyStr =  IOUtils.toString(is, "UTF-8");

            jsonArray = JSON.parseArray(responsBodyStr, PullRequests.class);

            for (PullRequests pr: jsonArray) {
                if (githubPullRequests.size() > n) {
                   break;
                }

                githubPullRequests.add(new GithubPullRequest(pr.getNumber(), pr.getUser().getLogin(), pr.getTitle()));
            }

            String csv = listToCsvString(githubPullRequests);

            FileUtils.writeStringToFile(file, csv, "UTF-8");

            EntityUtils.consume(entity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }

    }

    public static String listToCsvString(List<GithubPullRequest> list) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        String csv = "";
        for (GithubPullRequest githubPullRequest: list) {
            Field[] fields = githubPullRequest.getClass().getDeclaredFields();
           if (csv.length() == 0) {
              csv += getCsvTitle(fields);
           }

          csv += getCsvContent(fields, githubPullRequest);
        }
        System.out.println(csv);
        return csv;
    }

    public static String getCsvTitle(Field[] fields) {

        StringBuilder sb = new StringBuilder();
        for (Field field: fields) {
            if (sb.length() != 0) {
                sb.append(',');
            }
            sb.append(field.getName());
        }
        sb.append('\n');
        return sb.toString();
    }

    public static String getCsvContent(Field[] fields, GithubPullRequest githubPullRequest) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        StringBuilder sb = new StringBuilder();
        for (Field field: fields) {
            if (sb.length() != 0) {
                sb.append(',');
            }

            String filedName = field.getName();
            String getMethodName = "get" + filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
            Class clazz = githubPullRequest.getClass();
            Method getMethod = clazz.getMethod(getMethodName);
            Object value = getMethod.invoke(githubPullRequest);
            if (value == null) {
                continue;
            }

            sb.append(value.toString());
        }
        sb.append('\n');
        return sb.toString();
    }
}
