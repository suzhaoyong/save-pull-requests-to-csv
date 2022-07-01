package com.github.hcsp.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        repo = "https://api.github.com/repos/" + repo + "/pulls";
        List<String> issues = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(repo);
        HttpResponse httpResponse = httpclient.execute(httpGet);
        System.out.println(httpResponse.getStatusLine());
        HttpEntity entity = httpResponse.getEntity();
        InputStream inputStream = entity.getContent();
        List<String> contentList = IOUtils.readLines(inputStream, "UTF-8");
        JSONArray contentArray = (JSONArray) JSONValue.parse(contentList.get(0));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));
        CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT);
        csvPrinter.printRecord("number", "author", "title");
        for (int i = 0; i < n; i++) {
            JSONObject issue = (JSONObject) contentArray.get(i);
            int number = ((Long) issue.get("number")).intValue();
            String title = (String) issue.get("title");
            String name = (String) ((JSONObject) issue.get("user")).get("login");
            Arrays.asList(number, name, title);
            csvPrinter.printRecord(Arrays.asList(number, name, title));
        }
        bufferedWriter.close();
    }
}
