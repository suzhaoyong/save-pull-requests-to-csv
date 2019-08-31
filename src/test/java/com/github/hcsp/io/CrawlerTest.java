package com.github.hcsp.io;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;

public class CrawlerTest {
    @Test
    public void test() throws Exception {
        File tmp = File.createTempFile("csv", "");
        Crawler.savePullRequestsToCSV("golang/go", 10, tmp);

        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(tmp)));
        List<String[]> lines = reader.readAll();
        Assertions.assertArrayEquals(lines.get(0), new String[] {"number", "author", "title"});
        Assertions.assertTrue(lines.size() > 10);

        String[] lastLine = lines.get(10);
        GHPullRequest pull =
                GitHub.connectAnonymously()
                        .getRepository("golang/go")
                        .getPullRequest(Integer.parseInt(lastLine[0]));

        Assertions.assertEquals(pull.getUser().getLogin(), lastLine[1]);
        Assertions.assertTrue(pull.getTitle().contains(lastLine[2]));
    }
}
