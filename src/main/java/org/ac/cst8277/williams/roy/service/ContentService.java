package org.ac.cst8277.williams.roy.service;

import lombok.extern.slf4j.Slf4j;
import org.ac.cst8277.williams.roy.model.Content;
import org.ac.cst8277.williams.roy.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public Mono<Content> createMessage(Content content) {
        Content subContent = new Content(null, content.getPublisher_id(), content.getContent());
        return contentRepository.save(subContent);
    }

    // find all content by a publisher who the user subscribes to
    public Flux<Content> findSubscriberContent(Integer subscriberId) {
        return contentRepository.findSubscriberContent(subscriberId);
    }
}