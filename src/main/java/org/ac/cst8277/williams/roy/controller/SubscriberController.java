package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.model.User;
import org.ac.cst8277.williams.roy.service.SubscriberService;
import org.ac.cst8277.williams.roy.service.TokenPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;
import java.util.UUID;

@RestController
@RequestMapping("/sub")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private TokenPublishService tokenPublishService;

    @Autowired
    private ReactiveRedisOperations<String, Content> contentTemplate;

    @Autowired
    private ReactiveRedisOperations<String, Subscriber> tokenTemplate;

    @PostConstruct
    private void initMessageReceiver() {
        this.contentTemplate
                .listenTo(ChannelTopic.of("messages"))
                .map(ReactiveSubscription.Message::getMessage).subscribe(content -> {
                    createMessage(content).subscribe();
                });
    }

    @PostMapping("/content/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Content> createMessage(@RequestBody Content content) {
        return subscriberService.createMessage(content);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        subscriber.setId(UUID.randomUUID().toString());
        Mono<Subscriber> savedSubscriber = subscriberService.createSubscriber(subscriber);
        return savedSubscriber.mapNotNull(sub -> {
            if (sub != null && sub.getUser_id() != null) {
                tokenPublishService.initWebClient(sub.getUser_id());
                tokenPublishService.publish();
            }
            return sub;
        });
    }

    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SubscribedTo> subscribe(@RequestBody SubscribedTo subscribedTo) {
        return subscriberService.subscribe(subscribedTo);
    }

    @GetMapping("/{subscriberId}")
    public Mono<ResponseEntity<Subscriber>> findById(@PathVariable String subscriberId) {
        Mono<Subscriber> subscriber = subscriberService.findById(subscriberId);
        return subscriber.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/findPublishers/{subscriberId}")
    public Flux<SubscribedTo> findAllPublishers(@PathVariable Integer subscriberId) {
        return subscriberService.findAllPublishers(subscriberId);
    }

    @GetMapping("/findSubscribers/{publisherId}")
    public Flux<SubscribedTo> findAllSubscribers(@PathVariable Integer publisherId) {
        return subscriberService.findAllSubscribers(publisherId);
    }

    @GetMapping("/content/all/{subscriberId}/{publisherId}")
    // find all content by a publisher who the user subscribes to
    public Flux<Content> findSubscriberContent(@PathVariable Integer subscriberId, @PathVariable Integer publisherId) {
        return subscriberService.findSubscriberContent(subscriberId, publisherId);
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
