package me.pood1e.vmusic.server.manager.impl;

import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.data.SyncState;
import me.pood1e.vmusic.server.dao.SongRepository;
import me.pood1e.vmusic.server.manager.SongManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author pood1e
 */
@Service
public class SongManagerImpl extends BaseMongoManagerImpl<Song> implements SongManager {

    private final SongRepository repository;

    public SongManagerImpl(SongRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Flux<Song> findByIds(List<String> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public Flux<Song> saveBatch(List<Song> songs) {
        return repository.saveAll(songs);
    }

    @Override
    public Mono<Song> findOneToFix() {
        return repository.findTopByUrlIsNullAndStateIsNot(SyncState.NO_SOURCE);
    }

    @Override
    public Mono<Song> findOneToLocal() {
        return repository.findTopByStateIsNotAndSourceIsNotAndUrlIsNotNull(SyncState.NO_SOURCE, OutSource.LOCAL);
    }
}
