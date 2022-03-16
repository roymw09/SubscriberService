package org.ac.cst8277.williams.roy.service;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.ac.cst8277.williams.roy.model.Subscriber;
import org.ac.cst8277.williams.roy.repository.ContentRepository;
import org.ac.cst8277.williams.roy.repository.SubscribedToRepository;
import org.ac.cst8277.williams.roy.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;

@Service
@Slf4j
@Transactional
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private SubscribedToRepository subscribedToRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    private ReactiveRedisOperations<String, Content> reactiveRedisTemplate;

    @PostConstruct
    private void init() {
        this.reactiveRedisTemplate
                .listenTo(ChannelTopic.of("messages"))
                .map(ReactiveSubscription.Message::getMessage)
                .subscribe(System.out::println);

    }

    public Mono<Content> createContent(Content content) { return contentRepository.save(content); }

    public Mono<Subscriber> createSubscriber(Subscriber subscriber) {
        return subscriberRepository.save(subscriber);
    }

    public Mono<SubscribedTo> subscribe(SubscribedTo subscribedTo) {
        return subscribedToRepository.save(subscribedTo);
    }

    public Mono<Subscriber> findById(Integer id) {
        return subscriberRepository.findById(id);
    }

    public Flux<SubscribedTo> findAllPublishers(Integer subscriberId) {
        return subscribedToRepository.findAllPublishers(subscriberId);
    }

    public Flux<SubscribedTo> findAllSubscribers(Integer publisherId) {
        return subscribedToRepository.findAllSubscribers(publisherId);
    }
}
