package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.service.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sub/subscriber")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        return subscriberService.createSubscriber(subscriber);
    }

    @GetMapping("/{subscriberId}")
    public Mono<Subscriber> findById(@PathVariable Integer subscriberId) {
        return subscriberService.findById(subscriberId);
    }
}
