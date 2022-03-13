package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/subService")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        return subscriberService.createSubscriber(subscriber);
    }

    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SubscribedTo> subscribe(@RequestBody SubscribedTo subscribedTo) {
        return subscriberService.subscribe(subscribedTo);
    }

    @GetMapping("/{subscriberId}")
    public Mono<ResponseEntity<Subscriber>> findById(@PathVariable Integer subscriberId) {
        Mono<Subscriber> subscriber = subscriberService.findById(subscriberId);
        return subscriber.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/findPublishers/{subscriberId}")
    public Flux<SubscribedTo> findAllPublishers(@PathVariable Integer subscriberId) {
        return subscriberService.findAllPublishers(subscriberId);
    }

    @GetMapping("findSubscribers/{publisherId}")
    public Flux<SubscribedTo> findAllSubscribers(@PathVariable Integer publisherId) {
        return subscriberService.findAllSubscribers(publisherId);
    }
}
