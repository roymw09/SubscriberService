package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/subService")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private ReactiveRedisOperations<String, Content> reactiveRedisTemplate;

    @PostConstruct
    private void init() {
        this.reactiveRedisTemplate
                .listenTo(ChannelTopic.of("messages"))
                .map(ReactiveSubscription.Message::getMessage).subscribe(content -> {
                    createMessage(content).subscribe();
                });
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Content> createMessage(Content content) {
        return subscriberService.createMessage(content);
    }

    @PostMapping("/sub/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        return subscriberService.createSubscriber(subscriber);
    }

    @PostMapping("/sub/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SubscribedTo> subscribe(@RequestBody SubscribedTo subscribedTo) {
        return subscriberService.subscribe(subscribedTo);
    }

    @GetMapping("/sub/{subscriberId}")
    public Mono<ResponseEntity<Subscriber>> findById(@PathVariable Integer subscriberId) {
        Mono<Subscriber> subscriber = subscriberService.findById(subscriberId);
        return subscriber.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/sub/findPublishers/{subscriberId}")
    public Flux<SubscribedTo> findAllPublishers(@PathVariable Integer subscriberId) {
        return subscriberService.findAllPublishers(subscriberId);
    }

    @GetMapping("/sub/findSubscribers/{publisherId}")
    public Flux<SubscribedTo> findAllSubscribers(@PathVariable Integer publisherId) {
        return subscriberService.findAllSubscribers(publisherId);
    }

    @GetMapping("/sub/content/all/{subscriberId}/{publisherId}")
    // find all content by a publisher who the user subscribes to
    public Flux<Content> findSubscriberContent(@PathVariable Integer subscriberId, @PathVariable Integer publisherId) {
        Flux<Content> contentFlux = subscriberService.findSubscriberContent(subscriberId, publisherId);
        return subscriberService.findSubscriberContent(subscriberId, publisherId);
    }
}
