
package com.github.hcsp.io;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
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
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题

    static class GitHubPullRequest {
        // Pull request的编号
        String number;
        // Pull request的标题
        String title;
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(String number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo, int page) throws IOException {
        //创建HttpClient客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建请求方式  post  get  http://localhost:8888/demo/test/

        String uri = "https://github.com/" + repo + "/pulls?page=" + page;
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        //相应结果
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);

        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        String html = IOUtils.toString(stream, "UTF-8");
        Document document = Jsoup.parse(html);
        Elements elements = document.select(".lh-condensed");
        List<GitHubPullRequest> list = new ArrayList();

        for (Element element : elements) {
            // Pull request的编号
            String number;
            // Pull request的标题
            String title;
            // Pull request的作者的GitHub id
            String author;
            title = element.select(".js-navigation-open").text();
            author = element.select(".muted-link").text();
            number = element.select(".opened-by").text().split(" ")[0].substring(1);
            list.add(new GitHubPullRequest(number, title, author));
            System.out.println(number);
        }
        EntityUtils.consume(entity);
        response.close();
        httpClient.close();
        return list;
    }

    public static void writeLinesToFile(List<String> lines, File file) throws IOException {
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter buffwrite = new BufferedWriter(fw);
        for (int i = 0; i < lines.size(); i++) {
            buffwrite.write(lines.get(i));
            if (i < lines.size() - 1) {
                buffwrite.write(',');
            }
        }
//        for (String line : lines) {
//            buffwrite.write(line);
//            buffwrite.write(',');
//        }
        buffwrite.write('\n');
        buffwrite.flush();
    }

    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<String> lines = Arrays.asList("number", "author", "title");
        writeLinesToFile(lines, csvFile);

        int index;
        int page = 1;
        int maxSize = getFirstPageOfPullRequests(repo, 1).size();

        index = n % maxSize;
        if (n < maxSize) {
            page = 1;
        } else {
            page = n / maxSize + 1;
        }

        List<GitHubPullRequest> list = new ArrayList<>();
        for (int i = 0; i < page; i++) {
            if (i + 1 == page) {
                list.addAll(getFirstPageOfPullRequests(repo, i + 1).subList(0, index));
            } else {
                list.addAll(getFirstPageOfPullRequests(repo, i + 1));
            }
            System.out.println(list);
        }

        for (int i = 0; i < list.size(); i++) {
            List<String> strList = new ArrayList<>();
            strList.add(list.get(i).getNumber());
            strList.add(list.get(i).getAuthor());
            strList.add(list.get(i).getTitle());
            writeLinesToFile(strList, csvFile);
        }
    }

    public static void main(String[] args) throws IOException {
        File projectDir = new File(System.getProperty("basedir", System.getProperty("user.dir")));
        File csvFile = new File(projectDir, "info.CSV");

        savePullRequestsToCSV("gradle/gradle", 30, csvFile);
    }
}
