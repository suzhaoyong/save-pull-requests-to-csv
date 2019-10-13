package com.github.hcsp.io;

import com.opencsv.CSVWriter;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException, org.json.simple.parser.ParseException {
        long number;
        String title;
        String author;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            HttpEntity entity1 = response1.getEntity();
            InputStream is = entity1.getContent();
            JSONParser jsonParser = new JSONParser();

            JSONArray jsonArray = (JSONArray) jsonParser.parse(
                    new InputStreamReader(is, "UTF-8"));
            FileWriter fileWriter = new FileWriter(csvFile);
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            csvWriter.writeNext(new String[]{"number", "author", "title"});
            for (int i = 0; i < jsonArray.size() && i < n; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                title = (String) jsonObject.get("title");
                number = (long) jsonObject.get("number");
                JSONObject user = (JSONObject) jsonObject.get("user");
                author = (String) user.get("login");
                csvWriter.writeNext(new String[]{number + "", author, title});
            }
            csvWriter.close();
            EntityUtils.consume(entity1);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            response1.close();
        }
    }
}
