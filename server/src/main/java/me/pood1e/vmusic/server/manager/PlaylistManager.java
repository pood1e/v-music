package me.pood1e.vmusic.server.manager;

import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.PlaylistDoc;
import me.pood1e.vmusic.server.manager.BaseMongoManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
public interface PlaylistManager extends BaseMongoManager<PlaylistDoc> {

    Flux<PlaylistDoc> queryAll();
}
