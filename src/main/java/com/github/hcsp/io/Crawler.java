package com.github.hcsp.io;

import com.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {

        try(FileWriter fileWriter = new FileWriter(csvFile)) {
            CSVWriter csvWriter = new CSVWriter(fileWriter);

            List<GitHubPullRequest> list = getFirstPageOfPullRequests(repo);
            csvWriter.writeNext(new String[]{"number", "author", "title"});
            for (int i = 0; i < n; i++) {
                GitHubPullRequest pr = list.get(i);
                csvWriter.writeNext(new String[]{String.valueOf(pr.number), pr.author, pr.title});

            }
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String author, String title) {
            this.number = number;
            this.author = author;
            this.title = title;
        }

        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", author='" + author + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/"+repo+"/pulls");

        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            int count = 0;
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();

            //获取流里面的数据
            InputStream content = entity1.getContent();
            String html = IOUtils.toString(content, "UTF-8");
            //解析页面
            Document parse = Jsoup.parse(html);

            //获取Pr内容
            ArrayList<Element> Pr = parse.select(".js-issue-row");
            ArrayList<String> PRlist = new ArrayList<>();
            ArrayList<GitHubPullRequest> list = new ArrayList<>();
            for (Element element : Pr) {
                GitHubPullRequest pulls = new GitHubPullRequest(
                        Integer.parseInt(element.attr("id").substring(6)),  //id
                        element.select(".muted-link").get(0).text(),  //作者
                        element.select(".js-navigation-open").get(0).text()  //标题

                );
                list.add(pulls);
            }
            return list;

        }

    }

}
