package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sub/subscriber")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        subscriberService.createSubscriber(subscriber).subscribe();
        return new ResponseEntity<>(subscriber, HttpStatus.CREATED);
    }

    @GetMapping("/{subscriberId}")
    public Mono<ResponseEntity<Subscriber>> findById(@PathVariable Integer subscriberId) {
        Mono<Subscriber> subscriber = subscriberService.findById(subscriberId);
        return subscriber.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
