package me.pood1e.vmusic.server.manager.impl;

import me.pood1e.vmusic.server.core.annotation.ManagerType;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import me.pood1e.vmusic.server.core.request.kuwo.KuwoApi;
import me.pood1e.vmusic.server.core.request.kuwo.KuwoUtils;
import me.pood1e.vmusic.server.manager.SourceManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
@Service
@ManagerType(OutSource.KUWO)
public class KuwoManager implements SourceManager {

    private final KuwoApi api;

    public KuwoManager(KuwoApi api) {
        this.api = api;
    }

    @Override
    public Mono<Playlist> searchPlaylist(String id) {
        return null;
    }

    @Override
    public Flux<Song> searchByKey(SearchKeywordQuery query) {
        return api.searchByKey(KuwoUtils.searchBody(query))
                .map(KuwoUtils::parseSearchResult)
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Song> fullSearchByKey(SearchKeywordQuery query) {
        return searchByKey(query).flatMap(this::fillInfo);
    }

    @Override
    public Mono<Song> fillInfo(Song song) {
        return api.fetchUrl(song.getMid())
                .doOnNext(node -> KuwoUtils.parseUrl(node, song))
                .thenReturn(song);
    }

    @Override
    public Mono<String> findUrlById(String id) {
        return api.fetchUrl(id).mapNotNull(KuwoUtils::getUrl);
    }
}
