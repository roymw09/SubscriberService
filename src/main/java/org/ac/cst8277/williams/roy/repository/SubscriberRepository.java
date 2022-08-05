package org.ac.cst8277.williams.roy.repository;

import org.ac.cst8277.williams.roy.model.Subscriber;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SubscriberRepository extends ReactiveCrudRepository<Subscriber, Integer> {
    @Query("SELECT * FROM subscriber WHERE user_id = :subscriberId")
    Mono<Subscriber> findById(@Param("subscriberId") Integer subscriberId);
}
