package com.github.hcsp.io;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
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
    ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
      @Override
      public String handleResponse(
        final HttpResponse response) throws ClientProtocolException, IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
          HttpEntity entity = response.getEntity();
          return entity != null ? EntityUtils.toString(entity) : null;
        } else {
          throw new ClientProtocolException("Unexpected response status: " + status);
        }
      }
    };
    String responseBody = httpclient.execute(httpGet, responseHandler);
    List<JSONObject> list = JSON.parseArray(responseBody, JSONObject.class);
    witerFile(list, csvFile, n);
  }

  public static void writeLinesToFile(List<String> lines, File file) throws IOException {
    FileUtils.writeLines(file, lines);
  }

  public static void witerFile(List<JSONObject> list, File file, int n) throws IOException {
    List<String> lines = new ArrayList<>();
    lines.add("number,author,title");
    for (int i = 0; i < n; i++) {
      JSONObject item = list.get(i);
      int number = item.getInteger("number");
      String title = item.getString("title");
      String author = item.getJSONObject("user").getString("login");
      lines.add(number + "," + author + "," + title);
    }
    writeLinesToFile(lines, file);
  }
}
