package me.pood1e.vmusic.server.core.request.netease;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

/**
 * @author pood1e
 */
@HttpExchange
public interface NeteaseApi {

    @PostExchange(value = "https://music.163.com/weapi/search/get",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Mono<JsonNode> searchByKey(@RequestBody String request);

    @PostExchange(value = "https://music.163.com/weapi/v3/song/detail",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Mono<JsonNode> searchDetailsByIds(@RequestBody String request);

    @PostExchange(value = "https://music.163.com/api/v6/playlist/detail",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Mono<JsonNode> searchPlaylist(@RequestBody String request);

}
