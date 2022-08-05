package org.ac.cst8277.williams.roy.service;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.ac.cst8277.williams.roy.repository.SubscribedToRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class SubscribedToService {

    private final SubscribedToRepository subscribedToRepository;

    public SubscribedToService(SubscribedToRepository subscribedToRepository) {
        this.subscribedToRepository = subscribedToRepository;
    }

    public Flux<SubscribedTo> findAllPublishers(Integer subscriberId) {
        return subscribedToRepository.findAllPublishers(subscriberId);
    }

    public Flux<SubscribedTo> findAllSubscribers(String publisherId) {
        return subscribedToRepository.findAllSubscribers(publisherId);
    }

    public Mono<SubscribedTo> subscribe(SubscribedTo subscribedTo) {
        return subscribedToRepository.save(subscribedTo);
    }
}
