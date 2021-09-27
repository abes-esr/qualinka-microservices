package fr.abes.findra;

import fr.abes.findra.domain.utils.LuceneSearch;
import fr.abes.findra.domain.utils.MapStructMapper;
import fr.abes.findra.domain.utils.StringOperator;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootTest
public class Find2Test {

    @Autowired
    StringOperator stringOperator;

    @Autowired
    WebClient.Builder webClientBuilder;

    @Autowired
    MapStructMapper mapStructMapper;

    @Autowired
    ResourceLoader resourceLoader;


    @Test
    void luceneSearch() throws ParseException {

        System.out.println(
                LuceneSearch.Search("Jules", "Paul~0.8")
        );

    }

    @Test
    void readFileFromGit() {
        String urlGit = "https://raw.githubusercontent.com/4duytran/test-github-api/main/default-req.properties";
        Resource resource = resourceLoader.getResource(urlGit);
        String fileName = urlGit.substring( urlGit.lastIndexOf("/")+1);
        System.out.println(fileName);
        Properties configProp = new Properties();

        try {
            if (resource.exists()) {
                InputStream in = resource.getInputStream();
                configProp.load(in);
                configProp.forEach( (k, v) -> {
                    System.out.println(k + ":" + v);
                });
            } else {
                System.out.println("Not found the file");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
