package me.pood1e.vmusic.server.controller;

import me.pood1e.vmusic.server.core.model.data.Association;
import me.pood1e.vmusic.server.manager.AssociationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
@RestController
@RequestMapping("/api/association")
public class AssociationController extends BaseMongoController<Association> {

    private final AssociationManager service;

    public AssociationController(AssociationManager service) {
        super(service);
        this.service = service;
    }

    @GetMapping("/playlist")
    public Flux<Association> findByPlaylist(@RequestParam String id) {
        return service.findByPlaylist(id);
    }

    @GetMapping("/playlist/count")
    public Mono<Long> count(@RequestParam String id) {
        return service.countByPlaylist(id);
    }
}
