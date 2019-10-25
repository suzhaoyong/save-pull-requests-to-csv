package com.github.hcsp.io;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            HttpEntity entity1 = response1.getEntity();
            InputStream is = entity1.getContent();
            String str = IOUtils.toString(is, "UTF-8");
            JSONArray jsonArray = JSONArray.parseArray(str);
            List<java.io.Serializable> list = new ArrayList<>();
            list.add("number,author,title");

            for (int i = 0; i < n; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int number = jsonObject.getIntValue("number");
                String author = jsonObject.getJSONObject("user").getString("login");
                String title = jsonObject.getString("title");
                list.add(number + "," + author + "," + title );
            }
            FileUtils.writeLines(csvFile, list);
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
    }

    public static void main(String[] args) throws IOException {
        savePullRequestsToCSV("golang/go", 10, new File("/C/Users/eraser/Desktop/test.csv"));
    }
}
