package me.pood1e.vmusic.server.manager.impl;

import me.pood1e.vmusic.server.core.annotation.ManagerType;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import me.pood1e.vmusic.server.core.request.tencent.TencentApi;
import me.pood1e.vmusic.server.core.request.tencent.TencentUtils;
import me.pood1e.vmusic.server.manager.SourceManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * @author pood1e
 */
@Service
@ManagerType(OutSource.TENCENT)
public class TencentManager implements SourceManager {
    private final TencentApi api;

    public TencentManager(TencentApi api) {
        this.api = api;
    }

    private Mono<List<Song>> searchInfo(SearchKeywordQuery query) {
        return api.cgi(TencentUtils.searchBody(query))
                .map(TencentUtils::parseSearchResult);
    }

    @Override
    public Mono<Playlist> searchPlaylist(String id) {
        return null;
    }

    @Override
    public Flux<Song> searchByKey(SearchKeywordQuery query) {
        return searchInfo(query).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Song> fullSearchByKey(SearchKeywordQuery query) {
        return searchInfo(query).flatMap(songs ->
                        api.cgi(TencentUtils.fetchUrl(songs.stream().map(Song::getMid).toList()))
                                .doOnNext(node -> TencentUtils.parseUrlResult(node, songs))
                                .thenReturn(songs))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Song> fillInfo(Song song) {
        return api.cgi(TencentUtils.fetchUrl(Collections.singletonList(song.getMid())))
                .doOnNext(node -> TencentUtils.parseUrlResult(node, Collections.singletonList(song)))
                .thenReturn(song);
    }

    @Override
    public Mono<String> findUrlById(String id) {
        return api.cgi(TencentUtils.fetchUrl(Collections.singletonList(id)))
                .mapNotNull(TencentUtils::getUrl);
    }

}
