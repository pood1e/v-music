package me.pood1e.vmusic.server.controller;

import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.manager.SongManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author pood1e
 */
@RestController
@RequestMapping("/api/song")
public class SongController extends BaseMongoController<Song> {

    private final SongManager service;

    public SongController(SongManager service) {
        super(service);
        this.service = service;
    }

    @PostMapping("/find")
    public Flux<Song> findByIds(@RequestBody List<String> ids) {
        return service.findByIds(ids);
    }
}
