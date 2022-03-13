package org.ac.cst8277.williams.roy.repository;

import org.ac.cst8277.williams.roy.model.SubscribedTo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SubscribedToRepository extends ReactiveCrudRepository<SubscribedTo, Integer> {
    // find all publishers a user is subscribed to
    @Query("SELECT * FROM subscribed_to WHERE subscriber_id = :subscriberId")
    Flux<SubscribedTo> findAllPublishers(@Param("subscriberId") Integer subscriberId);

    // find all subscribers who are subscribed to a publisher
    @Query("SELECT * FROM subscribed_to WHERE publisher_id = :publisherId")
    Flux<SubscribedTo> findAllSubscribers(@Param("publisherId") Integer publisherId);
}
