package fr.abes.attrrc;


import com.ulisesbocchio.jasyptspringboot.configuration.EnableEncryptablePropertiesConfiguration;
import fr.abes.attrrc.domain.entity.XmlRootRecord;
import fr.abes.attrrc.domain.repository.ReferenceAutoriteOracle;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import oracle.xdb.XMLType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import java.awt.print.Book;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Import(EnableEncryptablePropertiesConfiguration.class)
@Slf4j
public class AttrrcTest {

    @Autowired
    ConnectionFactory connectionFactory;

    @Autowired
    ReferenceAutoriteOracle referenceAutoriteOracle;


    @BeforeAll
    public static void before() {
        /*System.setProperty("org.xml.sax.driver", "com.sun.org.apache.xerces.internal.parsers.SAXParser");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty("javax.xml.parsers.SAXParserFactory","com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");*/
        //System.setProperty("oracle.jdbc.getObjectReturnsXMLType", "false");

    }

    @Test
    void loadOracle() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        /*referenceAutoriteOracle.getEntityWithPpn("04853658X")
                .subscribe(v -> {
                    System.out.println(v);
                }, System.out::println, countDownLatch::countDown);*/

        Mono.from(connectionFactory.create())
            .flatMapMany(connection -> Flux.from(connection
                                                    .createStatement("select DATA_XML from NOTICESBIBIO where ppn = '04853658X'")
                                                    .execute())
                                        .flatMap(result ->result.map((row, rowMetadata) -> row.get(0, XMLType.class)))
                                        .doOnNext(v -> {
                                            try {

                                                String xmlString = v.getString();
                                                JAXBContext jaxbContext = JAXBContext.newInstance(XmlRootRecord.class);
                                                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                                                XmlRootRecord xmlRootRecord = (XmlRootRecord) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

                                                xmlRootRecord.getDatafieldList().forEach(t -> {
                                                    System.out.println("Tag = " + t.getTag());
                                                    t.getSubfieldList().forEach(e -> {
                                                        System.out.println(e.getCode() + ":" + e.getSubfield());
                                                    });
                                                });

                                            } catch (SQLException | JAXBException e) {
                                                e.printStackTrace();
                                            }
                                        })
                                        .thenMany(connection.close()))
            .subscribe();

        //countDownLatch.await();
    }

    @Test
    void loadOracleFromService() throws InterruptedException {
        referenceAutoriteOracle.getEntityWithPpn("04853658X").subscribe(t -> {
            t.getDatafieldList().forEach(v -> {
                System.out.println("Tag = " + v.getTag());
                v.getSubfieldList().forEach(e -> {
                    System.out.println(e.getCode() + ":" + e.getSubfield());
                });
            });
        });

        referenceAutoriteOracle.getDomainAndCodeWithPpn("04853658X").subscribe(System.out::println);

    }
}
