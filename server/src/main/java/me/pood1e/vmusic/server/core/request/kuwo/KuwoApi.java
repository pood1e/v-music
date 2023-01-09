package me.pood1e.vmusic.server.core.request.kuwo;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author pood1e
 */
@HttpExchange
public interface KuwoApi {

    @GetExchange(value = "https://kuwo.cn/api/www/search/searchMusicBykeyWord")
    Mono<JsonNode> searchByKey(@RequestParam Map<String, String> params);

    @GetExchange(value = "https://www.kuwo.cn/api/v1/www/music/playUrl")
    Mono<JsonNode> fetchUrl(@RequestParam String mid);
}
