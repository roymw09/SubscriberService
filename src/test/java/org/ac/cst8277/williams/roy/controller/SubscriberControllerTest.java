package org.ac.cst8277.williams.roy.controller;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.model.User;
import org.ac.cst8277.williams.roy.repository.ContentRepository;
import org.ac.cst8277.williams.roy.repository.SubscribedToRepository;
import org.ac.cst8277.williams.roy.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
public class SubscriberControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SubscribedToRepository subscribedToRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private DatabaseClient databaseClient;

    private final String subIdOne = UUID.randomUUID().toString();
    private final String subIdTwo = UUID.randomUUID().toString();
    private final String subIdThree = UUID.randomUUID().toString();

    private List<Subscriber> getSubscriberData() {
        User test = new User();
        return Arrays.asList(new Subscriber(subIdOne, 1),
                new Subscriber(subIdTwo, 2),
                new Subscriber(subIdThree, 3));
    }

    private List<SubscribedTo> getSubscribedToData() {
        return Arrays.asList(new SubscribedTo(null, 1, 1),
                new SubscribedTo(null, 2, 1),
                new SubscribedTo(null, 3, 3));
    }

    private List<Content> getContentData() {
        return Arrays.asList(new Content(null, 1, "Test content"),
                new Content(null, 1, "More test content"));
    }

    @BeforeEach
    public void setup() {
        List<String> statements = Arrays.asList(
                "DROP TABLE IF EXISTS content;",
                "DROP TABLE IF EXISTS subscribed_to;",
                "DROP TABLE IF EXISTS subscriber;",
                "CREATE TABLE subscriber ( " +
                        "id VARCHAR(250), " +
                        "user_id INT NOT NULL, " +
                        "PRIMARY KEY (id)); ",

                "CREATE TABLE subscribed_to (" +
                        "id SERIAL, " +
                        "subscriber_id VARCHAR(25) NOT NULL, " +
                        "publisher_id VARCHAR(250) NOT NULL," +
                        "PRIMARY KEY (id), " +
                        "FOREIGN KEY (subscriber_id) REFERENCES subscriber(id));",

                "CREATE TABLE content (\n" +
                        "    id SERIAL,\n" +
                        "    publisher_id VARCHAR(25) NOT NULL,\n" +
                        "    content VARCHAR(500) NOT NULL,\n" +
                        "    PRIMARY KEY (id)\n" +
                        ");"
        );

        statements.forEach(it -> databaseClient.sql(it)
                .fetch()
                .rowsUpdated()
                .block());

        subscriberRepository.deleteAll()
                .thenMany(Flux.fromIterable(getSubscriberData()))
                .flatMap(subscriberRepository::save)
                .doOnNext(subscriber -> {
                    System.out.println("Subscriber inserted from controller test " + subscriber);
                })
                .blockLast();

        subscribedToRepository.deleteAll()
                .thenMany(Flux.fromIterable(getSubscribedToData()))
                .flatMap(subscribedToRepository::save)
                .doOnNext(subscribedTo -> {
                    System.out.println("SubscribedTo inserted from controller test " + subscribedTo);
                })
                .blockLast();

        contentRepository.deleteAll()
                .thenMany(Flux.fromIterable(getContentData()))
                .flatMap(contentRepository::save)
                .doOnNext(content -> {
                    System.out.println("Content inserted from controller test " + content);
                })
                .blockLast();
    }

    @Test
    public void getSubscriberById() {
        webTestClient.get().uri("/sub".concat("/{subscriberId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.user_id", subIdOne);
    }

    @Test
    public void getSubscriberById_NotFound() {
        webTestClient.get().uri("/sub".concat("/{subscriberId}"), "6")
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    @Test
    public void createMessage() {
        Content content = new Content(null, 1, "hello");
        webTestClient.post().uri("/sub/content/create").contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(content), Content.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.content").isEqualTo("hello");
    }

    @Test
    public void getContentForSubscriber() {
        webTestClient.get().uri("/sub".concat("/content/all/{subscriberId}/{publisherId}"), subIdOne, "5")
                .exchange()
                .expectBody()
                .jsonPath("$.publisher_id", "5");
    }

    @Test
    public void createSubscriber() {
        Subscriber subscriber = new Subscriber(null, 5);
        webTestClient.post().uri("/sub/create").contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(subscriber), Subscriber.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.user_id").isEqualTo("5");
    }

    @Test
    public void subscribeToUser() {
        SubscribedTo subscribedTo = new SubscribedTo(null, 2, 3);
        webTestClient.post().uri("/sub/subscribe").contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(subscribedTo), Subscriber.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.subscriber_id").isEqualTo(subIdOne)
                .jsonPath("$.publisher_id").isEqualTo("5");
    }

    @Test
    public void getAllPublishersBySubscriberId(){
        webTestClient.get().uri("/sub".concat("/findPublishers/{subscriberId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.user_id", subIdOne);
    }

    @Test
    public void getAllSubscribersByPublisherId() {
        webTestClient.get().uri("/sub".concat("/findSubscribers/{publisherId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.user_id", subIdOne);
    }
}
