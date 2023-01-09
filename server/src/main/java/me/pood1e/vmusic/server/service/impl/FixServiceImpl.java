package me.pood1e.vmusic.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.pood1e.vmusic.server.core.annotation.ManagerType;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.data.SyncState;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import me.pood1e.vmusic.server.core.util.MatchUtils;
import me.pood1e.vmusic.server.manager.SongManager;
import me.pood1e.vmusic.server.manager.SourceManager;
import me.pood1e.vmusic.server.service.FixService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * @author pood1e
 */
@Slf4j
@Service
public class FixServiceImpl implements FixService {

    private final List<SourceManager> managers;
    private final Map<OutSource, SourceManager> managerMap;
    private final SongManager songManager;
    private final WebClient client;
    @Value("${app.save-path}")
    private String path;

    public FixServiceImpl(Collection<SourceManager> sourceManagers, SongManager songManager, WebClient client) {
        this.client = client;
        this.managerMap = new HashMap<>();
        this.managers = sourceManagers.stream().sorted(Comparator.comparingInt(manager ->
                manager.getClass().getAnnotation(ManagerType.class).value().ordinal())).toList();
        this.songManager = songManager;
        sourceManagers.forEach(manager -> managerMap.put(manager.getClass().getAnnotation(ManagerType.class).value(), manager));
    }

    @Scheduled(fixedDelay = 1000L)
    @Override
    public void fix() {
        songManager.findOneToFix()
                .flatMap(song -> searchBySong(song)
                        .defaultIfEmpty(song)
                        .flatMap(ret -> {
                            ret.setId(song.getId());
                            ret.setState(ret.getUrl() == null ? SyncState.NO_SOURCE : SyncState.FIXED);
                            log.info("{} fetch url {}", ret.getName(), ret.getUrl() != null);
                            return songManager.update(ret);
                        }))
                .block();
    }

    @Scheduled(fixedDelay = 1000L)
    @Override
    public void local() {
        songManager.findOneToLocal().doOnNext(song -> {
            Mono<byte[]> dataMono = getData(song.getUrl())
                    .onErrorResume(throwable -> fetchUrl(song).flatMap(this::getData));
            byte[] data = dataMono.share().block();

            String filename = song.getId() + ".mp3";
            try {
                Files.write(Path.of(path, filename),
                        Objects.requireNonNull(data),
                        StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (Files.exists(Path.of(path + filename))) {
                song.setSource(OutSource.LOCAL);
                song.setUrl("/api/local/" + filename);
                song.setState(SyncState.LOCALED);
            } else {
                song.setState(SyncState.NO_SOURCE);
            }
            songManager.update(song).block();
            log.info("{} local {}", song.getName(), song.getState());
        }).subscribeOn(Schedulers.boundedElastic()).block();
    }

    private Mono<String> fetchUrl(Song song) {
        return managerMap.get(song.getSource()).findUrlById(song.getMid());
    }

    private Mono<byte[]> getData(String url) {
        return client.get()
                .uri(url)
                .retrieve()
                .bodyToMono(byte[].class);
    }

    private Mono<Song> searchBySong(Song song) {
        SearchKeywordQuery query = SearchKeywordQuery.builder()
                .keyword(song.getName())
                .page(1)
                .pageSize(20)
                .build();
        Mono<Song> mono = Mono.empty();
        for (SourceManager manager : managers) {
            if (manager.getClass().getAnnotation(ManagerType.class).value().equals(song.getSource())) {
                continue;
            }
            mono = mono.switchIfEmpty(manager.searchByKey(query).filter(ret ->
                            MatchUtils.match(song, ret))
                    .next().flatMap(manager::fillInfo));
        }
        return mono;
    }
}
