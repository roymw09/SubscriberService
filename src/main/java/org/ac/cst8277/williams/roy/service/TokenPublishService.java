package org.ac.cst8277.williams.roy.service;

import org.ac.cst8277.williams.roy.model.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TokenPublishService {
    private String API_ENDPOINT = "http://localhost:8082/sub/getToken/";
    private WebClient webClient;

    @Autowired
    private ReactiveRedisOperations<String, Subscriber> tokenTemplate;

    public void initWebClient(Integer id) {
        this.webClient = WebClient.builder()
                .baseUrl(API_ENDPOINT + id)
                .build();
    }

    public void publish() {
        this.webClient.get()
                .retrieve()
                .bodyToMono(Subscriber.class)
                .flatMap(subscriber -> this.tokenTemplate.convertAndSend("subscriber_token", subscriber))
                .subscribe();
    }
}
