package me.pood1e.vmusic.server.core.request.kuwo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author pood1e
 */
public class KuwoUtils {

    public static Map<String, String> searchBody(SearchKeywordQuery query) {
        Map<String, String> map = new HashMap<>();
        map.put("key", query.getKeyword());
        map.put("pn", String.valueOf(query.getPage()));
        map.put("rn", String.valueOf(query.getPageSize()));
        return map;
    }

    public static List<Song> parseSearchResult(JsonNode root) {
        List<Song> songs = new ArrayList<>();
        ArrayNode nodes = (ArrayNode) root.get("data").get("list");
        nodes.forEach(node -> {
            if (node.get("isListenFee").asBoolean()) {
                return;
            }
            Set<String> singers = Set.of(StringEscapeUtils.unescapeHtml(node.get("artist").asText()).split("&"));
            songs.add(Song.builder()
                    .mid(node.get("rid").asText())
                    .name(StringEscapeUtils.unescapeHtml(node.get("name").asText()))
                    .source(OutSource.KUWO)
                    .authors(singers)
                    .duration(node.get("duration").asInt())
                    .cover(node.get("pic").asText())
                    .build());
        });
        return songs;
    }

    public static void parseUrl(JsonNode root, Song song) {
        song.setUrl(getUrl(root));
    }

    public static String getUrl(JsonNode root) {
        String url = root.get("data").get("url").asText();
        if (StringUtils.hasText(url)) {
            return url;
        }
        return null;
    }

}
