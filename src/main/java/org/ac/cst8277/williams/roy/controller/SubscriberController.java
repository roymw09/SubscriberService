package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.model.User;
import org.ac.cst8277.williams.roy.service.SubscriberService;
import org.ac.cst8277.williams.roy.service.RedisTokenPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RestController
@RequestMapping("/sub/subscriber")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private RedisTokenPublishService redisTokenPublishService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        subscriber.setId(UUID.randomUUID().toString());
        Mono<Subscriber> savedSubscriber = subscriberService.createSubscriber(subscriber);
        return savedSubscriber.mapNotNull(sub -> {
            if (sub != null && sub.getUser_id() != null) {
                redisTokenPublishService.initWebClient(sub.getUser_id());
                redisTokenPublishService.publish();
            }
            return sub;
        });
    }

    @GetMapping("/{subscriberId}")
    public Mono<ResponseEntity<Subscriber>> findById(@PathVariable String subscriberId) {
        Mono<Subscriber> subscriber = subscriberService.findById(subscriberId);
        return subscriber.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // check the users token through the UMS to verify that they have subscriber rights
    @GetMapping("/verify/{email}/{token}")
    public ResponseEntity<User> checkUserToken(@PathVariable("email") String email, @PathVariable("token") String token) {
        ResponseEntity<User> restTemplate;
        try {
            restTemplate = new RestTemplate().getForEntity(
                    "http://usermanagement-service:8081/users/" + email + "/" + token, User.class);
        } catch (HttpClientErrorException e) {
            restTemplate = ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(null);
        }
        return restTemplate;
    }

    @GetMapping("/getToken/{userId}")
    public Mono<ResponseEntity<Subscriber>> getSubscriberToken(@PathVariable("userId") Integer userId) {
        Mono<Subscriber> token = subscriberService.getSubscriberToken(userId);
        return token.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
