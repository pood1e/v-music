package me.pood1e.vmusic.server.manager;

import me.pood1e.vmusic.server.core.model.data.Song;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author pood1e
 */
public interface SongManager extends BaseMongoManager<Song> {
    Flux<Song> findByIds(List<String> ids);

    Flux<Song> saveBatch(List<Song> songs);

    Mono<Song> findOneToFix();

    Mono<Song> findOneToLocal();
}
