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
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String website = "https://github.com";
        String page = "pulls";
        List<String[]> result = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(String.format("%s/%s/%s", website, repo, page));
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            System.out.println(response.getStatusLine());
            //            HttpEntity是实体 body
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            String theString = IOUtils.toString(content, StandardCharsets.UTF_8);
            // 用第三方库JSoup解析HTML
            Document document = Jsoup.parse(theString);
            Elements selects_title = document.select(".js-issue-row");
            Elements selects_author = document.select(".opened-by"); // 不能用跟上面一样的selector, 因为author的位置会变化
            //   示例代码中的    EntityUtils.consume(entity1);
            for (int i = 0; i < selects_title.size(); i++) {
                String id = selects_title.get(i).attr("id").replaceAll("\\D+", ""); // 去除所有非数字
                String title = selects_title.get(i).child(0).child(1).child(0).text();
                String author = selects_author.get(i).child(1).text();
                // String href = select.child(0).child(1).child(0).attr("href");
                result.add(new String[]{id, author, title});
            }
        }
        writeToCSV(result, csvFile);
    }

    private static void writeToCSV(List<String[]> result, File csvFile) throws IOException {
        CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(csvFile)));
        writer.writeNext(new String[]{"number", "author", "title"});
        writer.writeAll(result);
        writer.close();
    }
}
