package com.github.hcsp.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        BufferedWriter bufw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile)));
        bufw.write("number"+",");
        bufw.write("author"+",");
        bufw.write("title");
        bufw.write('\n');

        try {
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(is);
            for (int i = 0; i < n; i++) {
                JsonNode number = node.get(i).get("number");
                JsonNode author = node.get(i).get("user").get("login");
                JsonNode title = node.get(i).get("title");
//                System.out.println(number +","+ author + "," + title);
                bufw.write(number +",");
                bufw.write(author +",");
                bufw.write(String.valueOf(title));
                bufw.write('\n');
            }
            bufw.flush();
            bufw.close();
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }
}
