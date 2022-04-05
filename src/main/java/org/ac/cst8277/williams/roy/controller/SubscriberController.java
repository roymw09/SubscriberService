package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.JwtRequest;
import org.ac.cst8277.williams.roy.model.JwtResponse;
import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.model.User;
import org.ac.cst8277.williams.roy.service.SubscriberService;
import org.ac.cst8277.williams.roy.service.RedisTokenPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) {
        Integer userId = subscriber.getUser_id();
        String username = new RestTemplate().getForObject("http://localhost:8081/users/user/getUsername/" + userId, String.class);
        JwtRequest tokenRequest = new JwtRequest(username, "password");
        tokenRequest.setUser_id(userId);
        HttpEntity<JwtRequest> jwtRequestEntity = new HttpEntity<>(tokenRequest);
        ResponseEntity<Subscriber> responseEntity;
        try {
            ResponseEntity<JwtResponse> response = new RestTemplate().postForEntity(
                    "http://localhost:8081/authenticate/subscriber", jwtRequestEntity, JwtResponse.class);
            subscriber.setId(response.getBody().getToken());
            subscriberService.createSubscriber(subscriber).subscribe();
            responseEntity = new ResponseEntity<>(subscriber, response.getStatusCode());
        } catch (HttpClientErrorException e) {
            responseEntity = new ResponseEntity<>(null, e.getStatusCode());
        }
        return responseEntity;
    }

    @GetMapping("/{subscriberId}")
    public Mono<ResponseEntity<Subscriber>> findById(@PathVariable Integer subscriberId) {
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
