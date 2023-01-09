package me.pood1e.vmusic.server.controller;

import me.pood1e.vmusic.server.core.model.data.BaseIdentify;
import me.pood1e.vmusic.server.manager.BaseMongoManager;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
public abstract class BaseMongoController<T extends BaseIdentify> {

    protected final BaseMongoManager<T> service;

    public BaseMongoController(BaseMongoManager<T> service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Mono<T> create(@RequestBody T t) {
        return service.create(t);
    }

    @PutMapping("/update")
    public Mono<T> update(@RequestBody T t) {
        return service.update(t);
    }

    @DeleteMapping("/delete")
    public Mono<Void> delete(@RequestParam String id) {
        return service.delete(id);
    }

    @GetMapping("/find")
    public Mono<T> queryById(@RequestParam String id) {
        return service.findById(id);
    }
}
