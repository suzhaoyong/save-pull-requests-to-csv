package com.github.hcsp.io;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httpGet);
        } catch (IOException e) {
            System.out.println("访问过于频繁，被禁了，过几分再试试");
            e.printStackTrace();
        }
        HttpEntity entity1 = response1.getEntity();
        InputStream is = entity1.getContent();
        String strArr = IOUtils.toString(is, "utf-8");
        List<Map<String, Object>> listObjectSec = JSONArray.parseObject(strArr, List.class);
        StringBuilder sb = new StringBuilder();
        sb.append("number,author,title\r\n");
        for (int i = 0; i < n; i++) {
            Map<String, Object> mapList = listObjectSec.get(i);
            String title = (String) mapList.get("title");
            String number = mapList.get("number").toString();
            Map<String, Object> userMap = (Map<String, Object>) mapList.get("user");
            String author = (String) userMap.get("login");
            sb.append(number + "," + author + "," + title + "\r\n");
        }
        FileUtils.writeStringToFile(csvFile, sb.toString(), Charset.defaultCharset());
        is.close();
    }

}
