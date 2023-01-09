package me.pood1e.vmusic.server.manager.impl;

import me.pood1e.vmusic.server.core.model.data.BaseIdentify;
import me.pood1e.vmusic.server.manager.BaseMongoManager;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
public abstract class BaseMongoManagerImpl<T extends BaseIdentify> implements BaseMongoManager<T> {

    protected final ReactiveMongoRepository<T, String> repository;

    public BaseMongoManagerImpl(ReactiveMongoRepository<T, String> repository) {
        this.repository = repository;
    }

    @Override
    public Mono<T> create(T t) {
        t.setId(null);
        return repository.save(t);
    }

    @Override
    public Mono<T> update(T t) {
        return repository.findById(t.getId())
                .flatMap(old -> repository.save(t));
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<T> findById(String id) {
        return repository.findById(id);
    }
}
