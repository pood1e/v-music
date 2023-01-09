package me.pood1e.vmusic.server.dao;

import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.PlaylistDoc;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pood1e
 */
@Repository
public interface PlaylistRepository extends ReactiveMongoRepository<PlaylistDoc, String> {
}
