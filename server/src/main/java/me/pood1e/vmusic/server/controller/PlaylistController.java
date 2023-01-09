package me.pood1e.vmusic.server.controller;

import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.PlaylistDoc;
import me.pood1e.vmusic.server.manager.PlaylistManager;
import me.pood1e.vmusic.server.service.PlaylistService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
@RestController
@RequestMapping("/api/playlist")
public class PlaylistController extends BaseMongoController<PlaylistDoc> {

    private final PlaylistManager manager;

    private final PlaylistService service;

    public PlaylistController(PlaylistManager manager, PlaylistService service) {
        super(manager);
        this.manager = manager;
        this.service = service;
    }

    @GetMapping("/all")
    public Flux<PlaylistDoc> findAll() {
        return manager.queryAll();
    }

    @PostMapping("/import")
    public Mono<Void> importPlaylist(@RequestParam OutSource source, @RequestParam String id) {
        return service.importPlaylist(source, id);
    }
}
