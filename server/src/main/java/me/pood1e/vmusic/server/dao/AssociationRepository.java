package me.pood1e.vmusic.server.dao;

import me.pood1e.vmusic.server.core.model.data.Association;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
@Repository
public interface AssociationRepository extends ReactiveMongoRepository<Association, String> {

    Flux<Association> findAllByPlaylist(String playlist);

    Mono<Long> countByPlaylist(String playlist);

}
