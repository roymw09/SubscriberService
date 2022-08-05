package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.ac.cst8277.williams.roy.service.SubscribedToService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sub/subscribedTo")
public class SubscribedToController {

    @Autowired
    private SubscribedToService subscribedToService;

    @PostMapping("/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SubscribedTo> subscribe(@RequestBody SubscribedTo subscribedTo) {
        return subscribedToService.subscribe(subscribedTo);
    }

    // find all the publishers a subscriber is subscribed to
    @GetMapping("/findPublishers/{subscriberId}")
    public Flux<SubscribedTo> findAllPublishers(@PathVariable Integer subscriberId) {
        return subscribedToService.findAllPublishers(subscriberId);
    }

    // find all subscribers who subscribe to a publisher
    @GetMapping("/findSubscribers/{publisherId}")
    public Flux<SubscribedTo> findAllSubscribers(@PathVariable String publisherId) {
        return subscribedToService.findAllSubscribers(publisherId);
    }
}
