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
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        HttpEntity entity1 = response1.getEntity();
//        System.out.println(entity1.getContent());
        InputStream is = entity1.getContent();
        String prContent = IOUtils.toString(is, "UTF-8");
//        System.out.println(PR.class);

        List<PR> prList = JSON.parseArray(prContent, PR.class); // 得到pr的数组
        List<PR> limitPrList = new ArrayList<>(); // n个pr
        int max = Math.max(prList.size(), n);
        for (int i = 0; i < max; i++) {
            limitPrList.add(prList.get(i));
        }
        writeCSV(limitPrList, csvFile);
        EntityUtils.consume(entity1);
    }

    public static void main(String[] args) throws IOException {
        savePullRequestsToCSV("golang/go", 10, new File("123"));
    }

    public static void writeCSV(List<PR> list, File file) throws IOException {
        StringBuffer stringInfo = new StringBuffer();
        stringInfo.append("number,author,title"+"\n");

        for (int i = 0; i < list.size(); i++) {
            stringInfo.append(list.get(i).getNumber() + ",");
            stringInfo.append(list.get(i).getUser().getLogin() + ",");
            stringInfo.append(list.get(i).getTitle());
            stringInfo.append("\n");
        }
        FileUtils.writeStringToFile(file, stringInfo.toString());
    }

    public static class PR {
        private String title;
        private int number;
        private User user;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

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

        public PR(String title, int number, User user) {
            this.title = title;
            this.number = number;
            this.user = user;
        }

        @Override
        public String toString() {
            return "PR{" +
                    "title='" + title + '\'' +
                    ", number=" + number +
                    ", user=" + user +
                    '}';
        }
    }

    public static class User {
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
}
