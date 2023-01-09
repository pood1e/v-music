package me.pood1e.vmusic.server.core.request.kugou;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author pood1e
 */
@HttpExchange
public interface KugouApi {

    @GetExchange(value = "https://complexsearch.kugou.com/v2/search/song",
            accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<JsonNode> searchByKey(@RequestParam Map<String, String> params);

    @GetExchange(value = "https://wwwapi.kugou.com/yy/index.php")
    Mono<JsonNode> fetchUrlById(@RequestParam Map<String, String> params);
}
