package com.github.hcsp.io;

import com.alibaba.fastjson.JSONArray;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String url = "https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=" + n;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            String headerBody = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            JSONArray jsonArray = JSONArray.parseArray(headerBody);
            List<String[]> csvList = new ArrayList<>();
            csvList.add(new String[]{"number", "author", "title"});
            for (int i = 0; i < n; i++) {
                String number = jsonArray.getJSONObject(i).getString("number");
                String title = jsonArray.getJSONObject(i).getString("title");
                String author = jsonArray.getJSONObject(i).getJSONObject("user").getString("login");
                csvList.add(new String[]{number, author, title});
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
            for (String[] array : csvList) {
                bw.write(StringUtils.join(array, ","));
                bw.newLine();
                bw.flush();
            }
            bw.close();
        }
    }
}
