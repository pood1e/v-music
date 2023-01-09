package me.pood1e.vmusic.server.manager;

import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
public interface SourceManager {

    Mono<Playlist> searchPlaylist(String id);

    Flux<Song> searchByKey(SearchKeywordQuery query);

    Flux<Song> fullSearchByKey(SearchKeywordQuery query);

    Mono<Song> fillInfo(Song song);

    Mono<String> findUrlById(String id);
}
