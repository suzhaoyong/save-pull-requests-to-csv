package com.github.hcsp.io;

import com.google.common.base.Splitter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String url = "https://github.com/" + repo + "/pulls";
        List<String> results = new ArrayList<>();

        results.add("number,author,title");
        String html = getUrlResponse(url);

        parseHtml(results, html);

        results.subList(0, n);

        Files.write(Paths.get(csvFile.getAbsolutePath()), results);
    }

    private static void parseHtml(List<String> results, String html) {
        Document document = Jsoup.parse(html);

        Elements elements = document.getElementsByClass("js-issue-row");

        for (Element element : elements) {

            String title = element.select(".link-gray-dark").text();

            String tempNum = element.select(".link-gray-dark").attr("id");

            String numberStr = Splitter.on("_").trimResults().splitToList(tempNum).get(1);

            Integer number = Integer.valueOf(numberStr);

            String author = element.select(".opened-by").select(".muted-link").text();

            StringBuffer sb = new StringBuffer().append(numberStr).append(",").append(author).append(",").append(title);

            results.add(sb.toString());
        }

    }

    private static String getUrlResponse(String url) throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();

    }
}
