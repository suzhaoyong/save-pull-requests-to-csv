package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpClient.execute(httpGet);
//        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();
        InputStream is = httpEntity.getContent();
        String result = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
        JSONArray jsonArray = JSON.parseArray(result);
        FileWriter fileWriter = new FileWriter(csvFile, true);
        fileWriter.write("number" + ",");
        fileWriter.write("author" + ",");
        fileWriter.write("title" + "\n");
        int i = 0;
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            int id = jsonObject.getInteger("number");
            fileWriter.write(id + ",");
            String author = jsonObject.getJSONObject("user").getString("login");
            fileWriter.write(author + ",");
            String title = jsonObject.getString("title");
            fileWriter.write(title + "\n");
            i++;
            if (i >= n) {
                break;
            }
        }
        fileWriter.flush();
        fileWriter.close();
    }

}
