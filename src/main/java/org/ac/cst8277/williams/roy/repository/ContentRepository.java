package org.ac.cst8277.williams.roy.repository;

import org.ac.cst8277.williams.roy.model.Content;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ContentRepository extends ReactiveCrudRepository<Content, Long> {
    // find all content by a publisher who the user subscribes to
    @Query("SELECT content.id, content.publisher_id, content.content " +
            "FROM content, subscribed_to " +
            "WHERE content.publisher_id = subscribed_to.publisher_id " +
            "AND subscribed_to.subscriber_id = :subscriberId ")
    Flux<Content> findSubscriberContent(@Param("subscriberId") String subscriberId);
}
