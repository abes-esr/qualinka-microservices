package fr.abes.findra.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertiesLoader {

    @Getter
    private final Properties configProp = new Properties();


    public PropertiesLoader(String fileName){

        log.info("Reading all properties from the file");
        try {
            InputStream in = loadSolrResource(fileName).getInputStream();
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Resource loadSolrResource(String fileName) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        /*String urlGit = "https://raw.githubusercontent.com/abes-esr/qualinka-findws-requests/main/"+ file+".properties";
        Resource resource = resourceLoader.getResource(urlGit);*/

        Resource resource = resourceLoader.getResource("classpath:solr-requetes/" + fileName +".properties");
        if (resource.exists()) {
            return resourceLoader.getResource("classpath:solr-requetes/" + fileName +".properties");
        } else {
            String urlGit = "https://raw.githubusercontent.com/abes-esr/qualinka-findws-requests/main/"+ fileName+".properties";
            return resourceLoader.getResource(urlGit);
        }

    }


}
