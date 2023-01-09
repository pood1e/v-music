package me.pood1e.vmusic.server.manager.impl;

import me.pood1e.vmusic.server.core.model.data.PlaylistDoc;
import me.pood1e.vmusic.server.manager.PlaylistManager;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author pood1e
 */
@Service
public class PlaylistManagerImpl extends BaseMongoManagerImpl<PlaylistDoc> implements PlaylistManager {

    public PlaylistManagerImpl(ReactiveMongoRepository<PlaylistDoc, String> repository) {
        super(repository);
    }

    @Override
    public Flux<PlaylistDoc> queryAll() {
        return repository.findAll();
    }
}
