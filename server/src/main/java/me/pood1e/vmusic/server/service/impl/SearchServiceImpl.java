package me.pood1e.vmusic.server.service.impl;

import me.pood1e.vmusic.server.core.annotation.ManagerType;
import me.pood1e.vmusic.server.manager.SourceManager;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import me.pood1e.vmusic.server.core.util.MatchUtils;
import me.pood1e.vmusic.server.service.SearchService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author pood1e
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final Map<OutSource, SourceManager> managerMap;
    private final List<SourceManager> managers;

    public SearchServiceImpl(Collection<SourceManager> sourceManagers) {
        this.managerMap = new HashMap<>();
        this.managers = sourceManagers.stream().sorted(Comparator.comparingInt(manager ->
                manager.getClass().getAnnotation(ManagerType.class).value().ordinal())).toList();
        sourceManagers.forEach(manager -> managerMap.put(manager.getClass().getAnnotation(ManagerType.class).value(), manager));
    }

    private SourceManager findSource(OutSource source) {
        SourceManager manager = managerMap.get(source);
        if (manager == null) {
            throw new RuntimeException();
        }
        return manager;
    }

    @Override
    public Mono<Song> searchBySong(Song song, OutSource except) {
        SearchKeywordQuery query = SearchKeywordQuery.builder()
                .keyword(song.getName())
                .page(1)
                .pageSize(20)
                .build();
        Mono<Song> mono = Mono.empty();
        for (SourceManager manager : managers) {
            if (manager.getClass().getAnnotation(ManagerType.class).value().equals(except)) {
                continue;
            }
            mono = mono.switchIfEmpty(manager.searchByKey(query).filter(ret -> MatchUtils.match(song, ret))
                    .next().flatMap(manager::fillInfo));
        }
        return mono;
    }

    @Override
    public Mono<Playlist> searchPlaylist(OutSource source, String id) {
        return findSource(source).searchPlaylist(id);
    }

    @Override
    public Flux<Song> searchBySource(OutSource source, SearchKeywordQuery query) {
        return findSource(source).fullSearchByKey(query);
    }
}
