package com.github.hcsp.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(csvFile));

        List<String[]> lines = new ArrayList<>();
        lines.add(new String[] { "number", "author", "title" });

        for (JsonNode pull: getFirstPageOfPullRequests(repo).subList(0, n)) {
            lines.add(new String[] { pull.get("number").asText(), pull.get("user").get("login").asText(), pull.get("title").asText() });
        }

        writer.writeAll(lines);
        writer.close();
    }

    private static List<JsonNode> getFirstPageOfPullRequests(String repo) throws IOException {
        List<JsonNode> result = new ArrayList<>();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(String.format("https://api.github.com/repos/%s/pulls", repo));
        httpGet.setHeader("Accept", "application/json");
        httpGet.addHeader("Content-Type", "application/json");
        CloseableHttpResponse response = client.execute(httpGet);

        JsonNode json = new ObjectMapper().readTree(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        Iterator<JsonNode> iterator = json.elements();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        client.close();
        return result;
    }
}
