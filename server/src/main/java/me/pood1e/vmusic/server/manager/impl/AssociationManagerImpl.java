package me.pood1e.vmusic.server.manager.impl;

import me.pood1e.vmusic.server.core.model.data.Association;
import me.pood1e.vmusic.server.dao.AssociationRepository;
import me.pood1e.vmusic.server.manager.AssociationManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author pood1e
 */
@Service
public class AssociationManagerImpl extends BaseMongoManagerImpl<Association> implements AssociationManager {

    private final AssociationRepository repository;

    public AssociationManagerImpl(AssociationRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Flux<Association> findByPlaylist(String playlist) {
        return repository.findAllByPlaylist(playlist);
    }

    @Override
    public Flux<Association> saveBatch(List<Association> associations) {
        return repository.saveAll(associations);
    }

    @Override
    public Mono<Long> countByPlaylist(String playlist) {
        return repository.countByPlaylist(playlist);
    }

}
