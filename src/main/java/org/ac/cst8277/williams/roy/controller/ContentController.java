package org.ac.cst8277.williams.roy.controller;

import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/sub/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @Autowired
    private ReactiveRedisOperations<String, Content> contentTemplate;

    @PostConstruct
    private void initMessageReceiver() {
        this.contentTemplate
                .listenTo(ChannelTopic.of("messages"))
                .map(ReactiveSubscription.Message::getMessage).subscribe(content -> {
                    createMessage(content);
                });
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Content> createMessage(@RequestBody Content content) {
        return contentService.createMessage(content);
    }

    @GetMapping("/all/{subscriberId}")
    // find all content by a publisher who the user subscribes to
    public Flux<Content> findSubscriberContent(@PathVariable Integer subscriberId) {
        return contentService.findSubscriberContent(subscriberId);
    }
}
