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
    SubscribedToService subscribedToService;

    @PostMapping("/subscribe/{token}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SubscribedTo> subscribe(@PathVariable("token") String token, @RequestBody SubscribedTo subscribedTo) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = new RestTemplate().
                    exchange("http://localhost:8081/authenticate/validate", HttpMethod.GET, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                subscribedToService.subscribe(subscribedTo).subscribe();
                return new ResponseEntity<>(subscribedTo, response.getStatusCode());
            } else {
                return new ResponseEntity<>(null, response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
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
