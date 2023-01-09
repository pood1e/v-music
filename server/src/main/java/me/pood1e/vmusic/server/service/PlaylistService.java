package me.pood1e.vmusic.server.service;

import me.pood1e.vmusic.server.core.model.data.OutSource;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
public interface PlaylistService {

    Mono<Void> importPlaylist(OutSource source, String id);
}
