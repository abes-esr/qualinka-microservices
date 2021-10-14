package fr.abes.attrrc.domain.service;

import fr.abes.attrrc.domain.dto.RCDto;

import fr.abes.attrrc.domain.entity.XmlRootRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Slf4j
@Service
public class AttrRCService {

    private final WebClient.Builder webClientBuilder;

    @Value("${solr.base-url}")
    private String solrBaseUrl;

    // This method returns filter function which will log request data
    // Using this for DEBUG mod
    private static ExchangeFilterFunction logRequestWebclient() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    public Mono<RCDto> attributs(String ppn) {

        String ppnBib = ppn.substring(0,ppn.indexOf("-"));
        int pos = Integer.parseInt(ppn.substring(ppn.indexOf("-")+1));

        RCDto rc = new RCDto();
        rc.setId(ppn);

        List<String> rameau = new ArrayList<>();
        List<String> subject600new = new ArrayList<>();
        List<String> subject600 = new ArrayList<>();
        List<String> subject601 = new ArrayList<>();
        List<String> subject602 = new ArrayList<>();
        List<String> subject604 = new ArrayList<>();
        List<String> subject605 = new ArrayList<>();
        List<String> subject607 = new ArrayList<>();
        List<String> subject608 = new ArrayList<>();
        List<String> dewey = new ArrayList<>();
        List<String> corporateBody = new ArrayList<>();
        List<String> cocontributor = new ArrayList<>();
        List<String> domain_code = new ArrayList<>();
        List<String> domain_lib = new ArrayList<>();
        List<String> otherIdDoc = new ArrayList<>();
        List<String> docLang = new ArrayList<>();
        List<String> originalDocLang = new ArrayList<>();
        List<String> publisher = new ArrayList<>();
        List<String> publisherPlace = new ArrayList<>();

        WebClient webClient = webClientBuilder.baseUrl("https://www.sudoc.fr/").build();
        //AtomicInteger counter = new AtomicInteger(0);

        return webClient.get().uri( uriBuilder -> uriBuilder
                .path(ppnBib + ".abes")
                .build() )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(XmlRootRecord.class)
                .doOnError(v -> log.error("ERROR => {}", v.getMessage()))
                .onErrorResume(v -> Mono.empty())
                .map(v -> {

                    v.getControlfieldList().stream().forEach(c -> {
                        if (c.getTag().equals("004")){
                            rc.setDateCreationNotice(c.getValue());
                        }
                    });

                    AtomicInteger posActu = new AtomicInteger(0);


                    v.getDatafieldList().stream().forEachOrdered(d -> {
                        String zone = d.getTag();
                        AtomicReference<String> strZone = new AtomicReference<>("");
                        AtomicReference<String> str600new = new AtomicReference<>("");

                        //Calcule la position en cours : à chaque 70X : position actuelle + 1
                        if (zone.startsWith("70") ) {
                            posActu.getAndIncrement();
                        }

                        d.getSubfieldList().stream().forEachOrdered(s -> {
                            String code = s.getCode();
                            String subfield = s.getSubfield();

                            //Citation
                            if (zone.equals("200")){
                                if (code.equals("a")){
                                    rc.setCitation(subfield);
                                }
                                else if (code.equals("f")){
                                    rc.setCitation(rc.getCitation() + " / " + subfield);
                                }
                            }
                            if (zone.equals("210")){
                                if (code.equals("a")) {
                                    rc.setCitation(rc.getCitation() + " / " + subfield);
                                }
                                else if (code.equals("c")) {
                                    rc.setCitation(rc.getCitation() + " : " + subfield);
                                }
                                else if (code.equals("d")) {
                                    rc.setCitation(rc.getCitation() + " , " + subfield);
                                }
                            }

                            //Title
                            if (zone.equals("200")) {
                                if (code.equals("a")) {
                                    if (rc.getTitle()==null) {
                                        rc.setTitle(subfield);
                                    }
                                    else {
                                        rc.setTitle(rc.getTitle()+" ; "+subfield); //TODO à tester
                                    }
                                }
                                if (code.equals("e")) {
                                    rc.setTitle(rc.getTitle() + " : " + subfield); //TODO à tester
                                }
                            }

                            if (zone.startsWith("70") && code.equals("3") && posActu.get()==pos) {
                                rc.setPpnAut(subfield);
                            }

                            if (zone.startsWith("70") && code.equals("4") && posActu.get()==pos) {
                                rc.setRole_code(subfield);
                            }

                            //Appelation
                            if (zone.startsWith("70") && posActu.get()==pos) {
                                if (code.equals("a")) {
                                    rc.setAppellation(subfield);
                                }
                                else if (code.equals("b")) {
                                    rc.setAppellation(rc.getAppellation()+ ", "+subfield);
                                }
                            }

                            //Rameau
                            if (zone.equals("606")) {
                                if (code.equals("a")) {
                                    strZone.set(subfield);
                                }
                                else if (code.equals("x") || code.equals("y") || code.equals("z")) {
                                    strZone.set(strZone.get()+" -- "+subfield);
                                }
                            }

                            //600new
                            if (zone.equals("600")) {
                                if (code.equals("a")) {
                                    str600new.set(subfield);
                                }
                                else if (code.equals("b") || code.equals("c") || code.equals("f")) {
                                    if (!str600new.get().contains("("))
                                        str600new.set(str600new.get()+ " (");
                                    str600new.set(str600new.get()+subfield);
                                }
                            }

                            //600 601 602 604 605 607
                            if (zone.equals("600") || zone.equals("601") || zone.equals("602") || zone.equals("604") || zone.equals("605") || zone.equals("607") || zone.equals("608")) {
                                if (code.equals("a")) {
                                    strZone.set(subfield);
                                }
                                else if (!(code.equals("a") || code.equals("2") || code.equals("3"))) {
                                    strZone.set(strZone.get() + ", " + subfield);
                                }

                                if (!(zone.equals("607") || zone.equals("608")) && code.equals("2")){
                                    strZone.set(strZone.get() + ", " + subfield);
                                }
                            }

                            //Dewey
                            if (zone.equals("676") && code.equals("a")) {
                                dewey.add(subfield);
                                rc.setDewey(dewey);
                            }

                            //OtherId
                            if (zone.equals("035") && code.equals("a")) {
                                otherIdDoc.add(subfield);
                                rc.setOtherIdDoc(otherIdDoc);
                            }

                            //DocLang
                            if (zone.equals("101") && code.equals("a")) {
                                docLang.add(subfield);
                                rc.setDocLang(docLang);
                            }

                            //OriginalDocLang
                            if (zone.equals("101") && code.equals("c")) {
                                originalDocLang.add(subfield);
                                rc.setOriginalDocLang(originalDocLang);
                            }

                            //Publisher
                            if ((zone.equals("210") && code.equals("c")) || (zone.equals("214") && code.equals("c"))) {
                                publisher.add(subfield);
                                rc.setPublisher(publisher);
                            }

                            if ((zone.equals("210") && code.equals("a")) || (zone.equals("214") && code.equals("a"))) {
                                publisherPlace.add(subfield);
                                rc.setPublisherPlace(publisherPlace);
                            }

                            if (zone.startsWith("71")){
                                if (code.equals("a")) {
                                    if (strZone.get().isEmpty()) {
                                        strZone.set(subfield);
                                    }
                                    else {
                                        strZone.set(strZone.get() + subfield + ", "); //TODO à tester
                                    }
                                }
                                else if (code.equals("b")) { //TODO à tester
                                    strZone.set(strZone.get() + subfield  + ". ");
                                }
                            }

                            if (zone.equals("105") && code.equals("a")) { //TODO à tester
                                if (subfield.length()>12){
                                    rc.setGenre(subfield.substring(11));
                                }
                            }

                            //Cocontributeurs
                            if (zone.startsWith("70") && posActu.get()!=pos) {
                                if (code.equals("a")) {
                                    strZone.set(subfield);
                                }
                                else if (code.equals("b")) {
                                    strZone.set(strZone.get() + ", " + subfield);
                                }
                            }

                            //TODO : publicationDate et originalPublicationDate

                        });
                        //Fin boucle sous zones

                        if (zone.equals("606") && !strZone.get().isEmpty()) {
                            rameau.add(strZone.get());
                            rc.setRameau(rameau);
                        }

                        if (zone.equals("600") ) {
                            subject600.add(strZone.get());
                            rc.setSubject600(subject600);

                            if (str600new.get().contains("("))
                                str600new.set(str600new.get()+")");
                            subject600new.add(str600new.get());
                            rc.setSubject600new(subject600new);
                        }

                        if (zone.equals("601") ) {
                            subject601.add(strZone.get());
                            rc.setSubject601(subject601);
                        }

                        if (zone.equals("602") ) {
                            subject602.add(strZone.get());
                            rc.setSubject602(subject602);
                        }

                        if (zone.equals("604") ) {
                            subject604.add(strZone.get());
                            rc.setSubject604(subject604);
                        }

                        if (zone.equals("605") ) {
                            subject605.add(strZone.get());
                            rc.setSubject605(subject605);
                        }

                        if (zone.equals("607") ) {
                            subject607.add(strZone.get());
                            rc.setSubject607(subject607);
                        }

                        if (zone.equals("608") ) {
                            subject608.add(strZone.get());
                            rc.setSubject608(subject608);
                        }

                        if (zone.startsWith("71")){
                            corporateBody.add(strZone.get());
                            rc.setCorporateBody(corporateBody);
                        }

                        if (zone.startsWith("70") && posActu.get()!=pos) {
                            cocontributor.add(strZone.get());
                            rc.setCocontributor(cocontributor);
                        }

                    });
                    return rc;
                })
                .onErrorResume(v -> Mono.just(new RCDto()));
    }

}
