package com.github.hcsp.io;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        HttpResponse response1 = httpclient.execute(httpGet);
        HttpEntity entity1 = response1.getEntity();
        String theString = IOUtils.toString(entity1.getContent(), "UTF-8");
        JSONArray jsonArray = JSONArray.parseArray(theString);

        BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
        bw.write("number,author,title\n");
        for (int i = 0; i < n; i++) {
            bw.write(jsonArray.getJSONObject(i).getString("number")+",");
            bw.write(jsonArray.getJSONObject(i).getJSONObject("user").getString("login")+",");
            bw.write(jsonArray.getJSONObject(i).getString("title")+"\n");
            bw.flush();
        }
    }
}
