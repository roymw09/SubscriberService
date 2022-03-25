package org.ac.cst8277.williams.roy.service;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    public Mono<Subscriber> createSubscriber(Subscriber subscriber) {
        return subscriberRepository.save(subscriber);
    }

    public Mono<Subscriber> findById(String id) {
        return subscriberRepository.findById(id);
    }

    public Mono<Subscriber> getSubscriberToken(Integer userId) {
        return subscriberRepository.getSubscriberToken(userId);
    }
}
