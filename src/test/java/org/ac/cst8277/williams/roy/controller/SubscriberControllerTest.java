package org.ac.cst8277.williams.roy.controller;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.ac.cst8277.williams.roy.model.Subscriber;
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
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private DatabaseClient databaseClient;

    private List<Subscriber> getSubscriberData() {
        return Arrays.asList(new Subscriber(null, 1),
                new Subscriber(null, 2),
                new Subscriber(null, 3));
    }

    private List<SubscribedTo> getSubscribedToData() {
        return Arrays.asList(new SubscribedTo(null, 1, 5),
                new SubscribedTo(null, 2, 5),
                new SubscribedTo(null, 3, 5));
    }

    @BeforeEach
    public void setup() {
        List<String> statements = Arrays.asList(
                "ALTER TABLE subscriber DROP CONSTRAINT CONSTRAINT_17;",
                "DROP TABLE IF EXISTS subscriber;",
                "DROP TABLE IF EXISTS subscribed_to;",
                "CREATE TABLE subscriber ( " +
                        "id SERIAL PRIMARY KEY, " +
                        "user_id INT NOT NULL, " +
                        "PRIMARY KEY (id)); ",

                "CREATE TABLE subscribed_to (" +
                        "id SERIAL PRIMARY KEY, " +
                        "subscriber_id INT NOT NULL, " +
                        "publisher_id INT NOT NULL," +
                        "PRIMARY KEY (id), " +
                        "FOREIGN KEY (subscriber_id) REFERENCES subscriber(id));"
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
    }

    @Test
    public void getSubscriberById() {
        webTestClient.get().uri("/subService".concat("/{subscriberId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.user_id", "1");
    }

    @Test
    public void getSubscriberById_NotFound() {
        webTestClient.get().uri("/subService".concat("/{subscriberId}"), "6")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void createSubscriber() {
        Subscriber subscriber = new Subscriber(null, 5);
        webTestClient.post().uri("/subService").contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(subscriber), Subscriber.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.user_id").isEqualTo("5");
    }

    @Test
    public void subscribeToUser() {
        SubscribedTo subscribedTo = new SubscribedTo(null, 1, 5);
        webTestClient.post().uri("/subService/subscribe").contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(Mono.just(subscribedTo), Subscriber.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.subscriber_id").isEqualTo("1")
                .jsonPath("$.publisher_id").isEqualTo("5");
    }

    @Test
    public void getAllPublishersBySubscriberId(){
        webTestClient.get().uri("/subService".concat("/findPublishers/{subscriberId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.user_id", "1");
    }

    @Test
    public void getAllSubscribersByPublisherId() {
        webTestClient.get().uri("/subService".concat("/findSubscribers/{publisherId}"), "1")
                .exchange()
                .expectBody()
                .jsonPath("$.user_id", "1");
    }
}
