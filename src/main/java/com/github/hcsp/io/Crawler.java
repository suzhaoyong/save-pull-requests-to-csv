package com.github.hcsp.io;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException, URISyntaxException {

        Document document = Jsoup.parse(getByRepoName(repo));

        Elements elements = document.select(".js-issue-row");

        List<Element> elementList = new ArrayList<>(elements);
        elementList = elementList.subList(0, n);

        List<Issue> issueList = new ArrayList<>();
        for (Element element : elementList) {
            String id = element.id();

            Pattern p = Pattern.compile("\\D");
            Matcher m = p.matcher(id);
            String number = m.replaceAll("").trim();

            String title = element.getElementById(id + "_link").text();

            String author = element.getElementsByAttributeValueContaining("data-hovercard-type", "user").text();

            Issue issue = new Issue();
            issue.setNumber(number);
            issue.setAuthor(author);
            issue.setTitle(title);

            issueList.add(issue);

        }

        writeCsv(issueList, csvFile);

    }

    public static void writeCsv(List<Issue> issueList, File csvFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(csvFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true));

        String firstLine = reader.readLine();

        if (firstLine == null) {
            writer.write("\"number\"" + "," + "\"author\"" + "," + "\"title\"");
            writer.newLine();
        }

        issueList.stream().forEach(issue -> {
            try {
                writer.write("\"" + issue.getNumber() + "\"" + "," + "\"" + issue.getAuthor() + "\"" + "," + "\"" + issue.getTitle() + "\"");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writer.close();
    }

    public static String getByRepoName(String repo) throws URISyntaxException, IOException {
        String url = String.format("https://github.com/%s/pulls", repo);

        URIBuilder uriBuilder = new URIBuilder(url);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        CloseableHttpResponse response = httpClient.execute(httpGet);

        HttpEntity entity = response.getEntity();

        InputStream is = entity.getContent();

        return IOUtils.toString(is, Charset.defaultCharset());
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        File projectDir = new File(System.getProperty("user.dir"));
        File testFile = new File(projectDir, "test.csv");

        if (!testFile.exists()) {
            testFile.createNewFile();
            savePullRequestsToCSV("gradle/gradle", 1, testFile);
        }

    }
}

class Issue {
    private String number;

    private String author;

    private String title;

    public void setNumber(String number) {
        this.number = number;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }
}

