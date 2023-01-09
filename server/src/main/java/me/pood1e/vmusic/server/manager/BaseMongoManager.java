package me.pood1e.vmusic.server.manager;

import me.pood1e.vmusic.server.core.model.data.BaseIdentify;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
public interface BaseMongoManager<T extends BaseIdentify> {

    Mono<T> create(T t);

    Mono<T> update(T t);

    Mono<Void> delete(String id);

    Mono<T> findById(String id);
}
