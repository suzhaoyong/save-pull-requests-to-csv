package com.github.hcsp.io;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        List<String> prlist = new ArrayList<String>();
        prlist.add("number,author,title");

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        HttpEntity entity1 = response1.getEntity();
        InputStream inputStream = entity1.getContent();
        JSONArray jsonArray = new JSONArray(IOUtils.toString(inputStream, Charset.defaultCharset()));
        try {
            System.out.println(response1.getStatusLine());
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        for (int i = 0; i < n; ++i) {
            Integer number = jsonArray.getJSONObject(i).getInt("number");
            String author = jsonArray.getJSONObject(i).getJSONObject("user").getString("login");
            String title = jsonArray.getJSONObject(i).getString("title");
            String combindedString = Integer.toString(number) + "," + author + "," + title;
            prlist.add(combindedString);
        }

//        File testFile = new File(System.getProperty("user.dir"), "target/test.txt");
        OutputStream outputStream = new FileOutputStream(csvFile);
        IOUtils.writeLines(prlist, "\n", outputStream, Charset.defaultCharset());
    }
}
