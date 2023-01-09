package me.pood1e.vmusic.server.manager.impl;

import me.pood1e.vmusic.server.core.annotation.ManagerType;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import me.pood1e.vmusic.server.core.request.migu.MiguApi;
import me.pood1e.vmusic.server.core.request.migu.MiguUtils;
import me.pood1e.vmusic.server.manager.SourceManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
@Service
@ManagerType(OutSource.MIGU)
public class MiguManager implements SourceManager {

    private final MiguApi api;

    public MiguManager(MiguApi api) {
        this.api = api;
    }

    @Override
    public Mono<Playlist> searchPlaylist(String id) {
        return null;
    }

    @Override
    public Flux<Song> searchByKey(SearchKeywordQuery query) {
        return api.searchByKey(MiguUtils.searchBody(query))
                .map(MiguUtils::parseSearchResult)
                .filter(songs -> !songs.isEmpty())
                .flatMap(songs ->
                        api.searchDurationByIds(MiguUtils.fetchDuration(songs.stream().map(Song::getMid).toList()))
                                .doOnNext(node -> MiguUtils.parseDurationResult(node, songs))
                                .thenReturn(songs))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Song> fullSearchByKey(SearchKeywordQuery query) {
        return searchByKey(query).flatMap(this::fillInfo);
    }

    @Override
    public Mono<Song> fillInfo(Song song) {
        if (song.getUrl() == null) {
            return api.searchAllUrlById(MiguUtils.fetchUrlBody(song.getMid()))
                    .doOnNext(node -> MiguUtils.parseUrl(node, song))
                    .thenReturn(song);
        }
        return Mono.just(song);
    }

    @Override
    public Mono<String> findUrlById(String id) {
        return api.searchUrlById(id)
                .mapNotNull(MiguUtils::getUrl);
    }
}
