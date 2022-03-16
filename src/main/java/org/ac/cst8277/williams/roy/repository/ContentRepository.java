package org.ac.cst8277.williams.roy.repository;

import org.ac.cst8277.williams.roy.model.Content;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ContentRepository extends ReactiveCrudRepository<Content, Long> {
    @Query("SELECT * FROM content WHERE publisher_id = :publisherId")
    Mono<Content> findBySubscriberId(@Param("publisherId") Integer publisherId);
}
