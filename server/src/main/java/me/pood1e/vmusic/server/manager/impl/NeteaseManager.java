package me.pood1e.vmusic.server.manager.impl;

import me.pood1e.vmusic.server.core.annotation.ManagerType;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import me.pood1e.vmusic.server.core.request.netease.NeteaseApi;
import me.pood1e.vmusic.server.core.request.netease.NeteaseUtils;
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
@ManagerType(OutSource.NETEASE)
public class NeteaseManager implements SourceManager {

    private final NeteaseApi api;

    public NeteaseManager(NeteaseApi api) {
        this.api = api;
    }

    private Mono<List<Song>> searchInfo(SearchKeywordQuery query) {
        return api.searchByKey(NeteaseUtils.searchBody(query))
                .map(NeteaseUtils::parseSearchResult);
    }

    @Override
    public Mono<Playlist> searchPlaylist(String id) {
        return api.searchPlaylist(NeteaseUtils.playlistBody(id))
                .map(NeteaseUtils::parsePlaylist).flatMap(playlist -> {
                    List<Song> songs = playlist.getSongs();
                    return api.searchDetailsByIds(NeteaseUtils.fetchUrlBody(songs.stream().map(Song::getMid).toList()))
                            .doOnNext(node -> NeteaseUtils.fillDetailInfo(node, songs))
                            .thenReturn(playlist);
                });
    }

    @Override
    public Flux<Song> searchByKey(SearchKeywordQuery query) {
        return searchInfo(query).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Song> fullSearchByKey(SearchKeywordQuery query) {
        return searchInfo(query).flatMap(songs ->
                        api.searchDetailsByIds(NeteaseUtils.fetchUrlBody(songs.stream().map(Song::getMid).toList()))
                                .doOnNext(node -> NeteaseUtils.fillUrlInfo(node, songs)).thenReturn(songs))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Song> fillInfo(Song song) {
        return api.searchDetailsByIds(NeteaseUtils.fetchUrlBody(Collections.singletonList(song.getMid())))
                .doOnNext(node -> NeteaseUtils.fillDetailInfo(node, Collections.singletonList(song)))
                .thenReturn(song);
    }

    @Override
    public Mono<String> findUrlById(String id) {
        return api.searchDetailsByIds(NeteaseUtils.fetchUrlBody(Collections.singletonList(id)))
                .mapNotNull(NeteaseUtils::getUrl);
    }
}
