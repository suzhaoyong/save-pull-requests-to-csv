package com.github.hcsp.io;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(String.format("https://api.github.com/repos/%s/pulls?per_page=%s", repo, n));
        HttpResponse response = httpClient.execute(httpget);

        List<String[]> list = new ArrayList<>();
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            JSONArray jsonArray = new JSONArray(result);
            list.add(new String[]{"number", "author", "title"});
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pull = jsonArray.getJSONObject(i);
                JSONObject user = pull.getJSONObject("user");
                list.add(new String[]{Integer.toString(pull.getInt("number")), user.getString("login"), pull.getString("title")});
            }

            CSVWriter writer = new CSVWriter(new FileWriter(csvFile));
            for (String[] i:list) {
                writer.writeNext(i);
            }
            writer.close();
        }
    }

    public static void main(String[] args) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        File tmp = File.createTempFile("csv", "");
        Crawler.savePullRequestsToCSV("golang/go", 10, tmp);


        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(tmp)));
        List<String[]> lines = reader.readAll();
    }
}
