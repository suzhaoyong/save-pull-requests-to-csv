package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) {
        String title, author;
        int number;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        final String Useragent = "Mozilla/5.0 (Windows NT 10.0; Win64;  x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36";
        final String repoUrl = "https://api.github.com/repos/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(repoUrl);
        httpGet.addHeader("User-Agent", Useragent);
        //创建httpGet
        try {
            StringBuilder sb = new StringBuilder("number,author,title\n");
            //创建StringBu来缓冲要得到的数据
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                String resultJson = EntityUtils.toString(entity, Charset.defaultCharset());
                //把response转换成实体，才能继续转成字符串

                JSONArray jArray = JSON.parseArray(resultJson);
                //利用阿里的库 JSONArray解析拿到的response

                for (int j = 0; j < n; j++) {
                    JSONObject object = (JSONObject) jArray.get(j);
                    //利用JSONObject 拿去需要的数据
                    number = object.getIntValue("number");
                    author = object.getJSONObject("user").getString("login");
                    title = object.getString("title");
                    sb.append(number).append(",").append(author).append(",").append(title).append("\n");
                }
                FileUtils.writeLines(csvFile, Collections.singleton(sb));
                //通过FileUtils 写入文件
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}


