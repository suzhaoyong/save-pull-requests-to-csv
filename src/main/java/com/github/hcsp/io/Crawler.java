package com.github.hcsp.io;

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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        //获取url
        String url = getUrl(repo);
        //获取html
        String html = getHtml(url);
        //抓取页面
        Document doc = crawlPage(html);
        //解析页面元素
        List<Record> records = parsePageElements(doc, n);
        //写入cvs
        writeToCvs(records, csvFile);
    }

    private static String getUrl(String repo) {
        return "https://github.com/" + repo + "/pulls";
    }

    private static String getHtml(String url) throws IOException {
        System.out.println("url is " + url);
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }
    }

    private static Document crawlPage(String html) {
        return Jsoup.parse(html);
    }

    private static List<Record> parsePageElements(Document doc, int n) {
        System.out.println(n);
        Elements elements = doc.select(".Box-row--drag-hide");
        List<Record> recordList = elements.stream()
                .map(Crawler::getRecord)
                .collect(Collectors.toList())
                .subList(0, n);
        ArrayList<Record> records = new ArrayList<>(recordList.size() + 1);
        records.add(Record.of("number", "author", "title"));
        records.addAll(recordList);
        return records;
    }

    private static Record getRecord(Element element) {
        Element titleTag = element.selectFirst(".js-navigation-open");
        String title = titleTag.text();
        Element idTag = element.selectFirst(".opened-by");
        String id = idTag.text();
        id = subStringId(id);
        Element namTag = element.selectFirst(".Link--muted");
        String name = namTag.text();
        return Record.of(id, name, title);
    }

    private static String subStringId(String id) {
        return id.substring(1, id.indexOf(" opened"));
    }

    private static void writeToCvs(List<Record> records, File csvFile) throws IOException {
        records.forEach(System.out::println);
        Path path = Paths.get(csvFile.getAbsolutePath());
        String string = records.stream().map(Record::toString).collect(Collectors.joining("\n"));
        Files.write(path, string.getBytes());
    }
}

class Record {
    private String number;
    private String author;
    private String title;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
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

    public static Record of(String id, String name, String title) {
        Record record = new Record();
        record.setNumber(id);
        record.setAuthor(name);
        record.setTitle(title);
        return record;
    }

    @Override
    public String toString() {
        return number + "," + author + "," + title;
    }
}
