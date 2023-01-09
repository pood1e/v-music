package me.pood1e.vmusic.server.dao;

import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.data.SyncState;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
@Repository
public interface SongRepository extends ReactiveMongoRepository<Song, String> {
    Mono<Song> findTopByUrlIsNullAndStateIsNot(SyncState state);

    Mono<Song> findTopByStateIsNotAndSourceIsNotAndUrlIsNotNull(SyncState state, OutSource source);
}
