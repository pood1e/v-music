package me.pood1e.vmusic.server.manager;

import me.pood1e.vmusic.server.core.model.data.Association;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author pood1e
 */
public interface AssociationManager extends BaseMongoManager<Association> {
    Flux<Association> findByPlaylist(String playlist);

    Flux<Association> saveBatch(List<Association> associations);

    Mono<Long> countByPlaylist(String playlist);
}
