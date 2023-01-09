package me.pood1e.vmusic.server.service;

import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
public interface SearchService {

    Mono<Song> searchBySong(Song song, OutSource source);

    Mono<Playlist> searchPlaylist(OutSource source, String id);

    Flux<Song> searchBySource(OutSource source, SearchKeywordQuery query);

}
