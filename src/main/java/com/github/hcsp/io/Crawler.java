package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    private static final String GITHUB_API = "https://api.github.com/repos/";
    private static final String GITHUB_END_PR = "/pulls";
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<Map<String, Object>> list = getRepoPRsTopN(getGitHubPRUrl(repo), n);
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
        writer.write("number,author,title\n");
        for (Map<String, Object> o : list) {
            String line = o.get("number") + "," + ((Map<String, String>) o.get("user")).get("login") + "," + o.get("title");
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
    public static List<Map<String, Object>> getRepoPRsTopN(String url, int n) throws IOException {
        String topTenParam = "?page=1&per_page=" + n;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + topTenParam);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            String json = IOUtils.toString(entity.getContent());
            return JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
            });
        } finally {
            response.close();
        }
    }
    private static String getGitHubPRUrl(String repo) {
        return GITHUB_API + repo + GITHUB_END_PR;
    }
}
