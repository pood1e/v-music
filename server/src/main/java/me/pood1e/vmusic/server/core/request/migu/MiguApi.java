package me.pood1e.vmusic.server.core.request.migu;

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
public interface MiguApi {

    @GetExchange(url = "https://m.music.migu.cn/migu/remoting/scr_search_tag")
    Mono<JsonNode> searchByKey(@RequestParam Map<String, String> params);

    @GetExchange(url = "https://music.migu.cn/v3/api/music/audioPlayer/songs")
    Mono<JsonNode> searchDurationByIds(@RequestParam Map<String, String> params);

    @GetExchange(url = "https://m.music.migu.cn/migu/remoting/cms_detail_tag")
    Mono<JsonNode> searchUrlById(@RequestParam String cpid);

    @GetExchange(url = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do")
    Mono<JsonNode> searchAllUrlById(@RequestParam Map<String, String> params);
}
