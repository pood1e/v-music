package me.pood1e.vmusic.server.core.request.tencent;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author pood1e
 */
@HttpExchange
public interface TencentApi {

    @PostExchange("https://u.y.qq.com/cgi-bin/musicu.fcg")
    Mono<JsonNode> cgi(@RequestBody JsonNode queryBody);

}
