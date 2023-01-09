package me.pood1e.vmusic.server.controller;

import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import me.pood1e.vmusic.server.service.SearchService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/{source}/keyword")
    public Flux<Song> keyword(@PathVariable String source, @RequestBody SearchKeywordQuery query) {
        return searchService.searchBySource(OutSource.valueOf(source.toUpperCase()), query);
    }
}
