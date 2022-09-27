package com.github.hcsp.io;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，读取前n个Pull request并保存至csvFile指定的文件中，格式如下：
    // number,author,title
    // 12345,blindpirate,这是一个标题
    // 12345,FrankFang,这是第二个标题
    public static void savePullRequestsToCSV(String repo, int n, File csvFile) throws IOException {
        String gitPullResponse = getFirstPageOfPullRequests(repo, n);
        List<GitHubPullRequest> requests = convertResponseBodyToList(gitPullResponse);
        String lists = StringUtils.join(requests, System.lineSeparator());
        FileUtils.writeStringToFile(csvFile, "number,author,title" + System.lineSeparator() + lists, Charset.defaultCharset());
    }

    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的 GitHub 用户名
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }

        @Override
        public String toString() {
            List<String> list = Arrays.asList(Integer.toString(this.number), this.author, this.title);
            return StringUtils.join(list, ",");
        }
    }

    static class User {
        public String login;
    }

    static class OriginalPullRequest {
        @JsonProperty("number")
        int number;
        // Pull request的标题
        @JsonProperty("title")
        String title;
        @JsonProperty("user")
        User user;
    }

    public static String getFirstPageOfPullRequests(String repo, int n) throws IOException {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpget = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?per_page=" + n);
            System.out.println("Executing request " + httpget.getMethod() + " " + httpget.getUri());
            // Create a custom response handler
            final HttpClientResponseHandler<String> responseHandler = response -> {
                final int status = response.getCode();
                if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                    final HttpEntity entity = response.getEntity();
                    try {
                        if (entity == null) {
                            return null;
                        }
                        return EntityUtils.toString(entity);
                    } catch (final ParseException ex) {
                        throw new ClientProtocolException(ex);
                    }
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            return httpclient.execute(httpget, responseHandler);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<GitHubPullRequest> convertResponseBodyToList(String entity) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(entity, new TypeReference<List<OriginalPullRequest>>() {
        }).stream().map(item -> new GitHubPullRequest(item.number, item.title, item.user.login)).collect(Collectors.toList());
    }
}
