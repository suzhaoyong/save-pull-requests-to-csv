package com.github.hcsp.io;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static class Pull {
        private String number;
        private String author;
        private String title;

        Pull(String number, String author, String title) {
            this.number = number;
            this.author = author;
            this.title = title;
        }

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
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        StringBuilder url = new StringBuilder();
        url.append("https://github.com/");
        url.append(repo);
        url.append("/pulls");
        String html = GetResponseHtml(url.toString());
        List<Pull> pullList = new ArrayList<Pull>();
        if (html != null && !html.isEmpty()) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select(".Box-row");

            for (int i = 0; i < n; i++) {
                Element el = elements.get(i);
                Element numberElemnt = el.select(".Box-row").select("div[class='mt-1 text-small text-gray']").select(">span").first();
                Element tilteElement = el.select(".Box-row").select("a[id*='issue']").first();
                Element authorElement = el.select(".Box-row").select("div[class='mt-1 text-small text-gray']")
                        .select(">span[class='opened-by']").select("a[class='muted-link']").first();
                if (numberElemnt != null && tilteElement != null && authorElement != null) {
                    String fullNumberText = numberElemnt.text();
                    String number = fullNumberText.substring(1, fullNumberText.indexOf("opened")).trim();
                    String author = authorElement.childNodes().get(0).toString();
                    String title = tilteElement.childNodes().get(0).toString();
                    Pull pull = new Pull(number, author, title);
                    pullList.add(pull);
                }
            }
        }
        if (pullList.size() > 0) {
            pullList.add(0, new Pull("number", "author", "title"));
            WriteDataToFile(pullList, csvFile);
        }

    }

    static String GetResponseHtml(String url) throws IOException {
        String html = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        try {
            HttpEntity entity1 = httpResponse.getEntity();
            InputStream is = entity1.getContent();
            html = IOUtils.toString(is, "UTF-8");
        } finally {
            httpResponse.close();
        }
        return html;
    }

    static void WriteDataToFile(List<Pull> pullList, File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        StringBuilder sb = new StringBuilder();
        for (Pull p : pullList
        ) {
            sb.append(p.getNumber());
            sb.append(",");
            sb.append(p.getAuthor());
            sb.append(",");
            sb.append(p.getTitle());
            sb.append("\r\n");
        }

        FileWriter writer = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write(sb.toString());
        bw.close();
    }


}



