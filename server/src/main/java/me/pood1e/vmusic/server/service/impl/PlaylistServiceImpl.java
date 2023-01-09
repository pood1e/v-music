package me.pood1e.vmusic.server.service.impl;

import jakarta.annotation.Resource;
import me.pood1e.vmusic.server.core.annotation.ManagerType;
import me.pood1e.vmusic.server.core.model.data.Association;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.PlaylistDoc;
import me.pood1e.vmusic.server.manager.AssociationManager;
import me.pood1e.vmusic.server.manager.PlaylistManager;
import me.pood1e.vmusic.server.manager.SongManager;
import me.pood1e.vmusic.server.manager.SourceManager;
import me.pood1e.vmusic.server.service.PlaylistService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pood1e
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final Map<OutSource, SourceManager> managerMap;

    @Resource
    private PlaylistManager playlistManager;
    @Resource
    private AssociationManager associationManager;
    @Resource
    private SongManager songManager;

    public PlaylistServiceImpl(Collection<SourceManager> sourceManagers) {
        this.managerMap = new HashMap<>();
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
    public Mono<Void> importPlaylist(OutSource source, String id) {
        return findSource(source).searchPlaylist(id).flatMap(playlist -> {
            PlaylistDoc doc = PlaylistDoc.builder()
                    .name(playlist.getName())
                    .build();
            return playlistManager.create(doc).flatMap(ret -> songManager.saveBatch(playlist.getSongs())
                    .collectList()
                    .flatMap(songs -> associationManager.saveBatch(songs.stream()
                                    .map(song -> Association.builder()
                                            .song(song.getId())
                                            .playlist(ret.getId())
                                            .build())
                                    .toList())
                            .collectList()));
        }).then();
    }
}
