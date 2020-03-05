package com.github.hcsp.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull
    // request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        int iPage = 1;
        int iPrCount = 0;
        String url = "https://github.com/" + repo + "/pulls?page=" + iPage + "&q=is%3Apr+is%3Aopen";
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("number").append(',').append("author").append(',').append("title").append('\n');
        Files.write(csvFile.toPath(), sBuilder.toString().getBytes(), StandardOpenOption.APPEND);
        do {
            Document doc = Jsoup.parse(getPullRequestsToCSV(url));
            Elements el = doc.select(".js-issue-row");
            for (Element issueItem : el) {
                String title = issueItem.select(".js-navigation-open").get(0).text();
                String[] strings = issueItem.select(".opened-by").get(0).text().split(" ");
                String name = strings[strings.length - 1];
                int id = Integer.valueOf(strings[0].substring(1));
                StringBuilder sBuilder1 = new StringBuilder();
                sBuilder1.append(id).append(',').append(name).append(',').append(title).append('\n');
                Files.write(csvFile.toPath(), sBuilder1.toString().getBytes(), StandardOpenOption.APPEND);
                iPrCount++;
                if (iPrCount >= n) {
                    return;
                }
            }
            iPage++;
        } while (true);
    }

    public static String getPullRequestsToCSV(String url) throws IOException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            RequestConfig defaultConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
            httpGet.setConfig(defaultConfig);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        File testFile = new File(projectDir, "target/test.txt");
        Crawler.savePullRequestsToCSV("golang/go", 10, testFile);
    }
}
