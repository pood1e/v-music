package me.pood1e.vmusic.server.core.request.kugou;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author pood1e
 */
public class KugouUtils {

    private static final String SPLITTER = "-";

    public static Map<String, String> searchBody(SearchKeywordQuery query) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("clientver", "2000");
        params.put("dfid", "-");
        params.put("keyword", query.getKeyword());
        params.put("mid", "-");
        params.put("page", String.valueOf(query.getPage()));
        params.put("pagesize", String.valueOf(query.getPageSize()));
        params.put("srcappid", "2919");
        params.put("userid", "-1");
        StringBuilder builder = new StringBuilder();
        builder.append("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt");
        params.forEach((k, v) -> builder.append(k).append("=").append(v));
        builder.append("NVPh5oo715z5DIWAeQlhMDsWXXQV4hwt");
        params.put("signature", DigestUtils.md5DigestAsHex(builder.toString().getBytes(StandardCharsets.UTF_8)));
        return params;
    }

    public static List<Song> parseSearchResult(JsonNode root) {
        List<Song> songs = new ArrayList<>();
        ArrayNode list = (ArrayNode) root.get("data").get("lists");
        list.forEach(node -> {
            Set<String> singers = Set.of(node.get("SingerName").asText().split("、"));
            String id = node.get("FileHash").asText();
            String albumId = node.get("AlbumID").asText();
            if (StringUtils.hasText(albumId)) {
                id = id + SPLITTER + albumId;
            }
            songs.add(Song.builder()
                    .authors(singers)
                    .source(OutSource.KUGOU)
                    .name(node.get("SongName").asText())
                    .mid(id)
                    .duration(node.get("Duration").asInt())
                    .build());
        });
        return songs;
    }

    public static Map<String, String> fetchUrl(String id) {
        Map<String, String> map = new HashMap<>();
        map.put("r", "play/getdata");
        map.put("mid", "-");
        String[] args = id.split(SPLITTER);
        map.put("hash", args[0]);
        if (args.length > 1) {
            map.put("album_id", args[1]);
        }
        return map;
    }

    public static void parseUrl(JsonNode root, Song song) {
        if (root.get("err_code").asInt() == 0) {
            song.setCover(root.get("data").get("img").asText());
            song.setUrl(root.get("data").get("play_url").asText());
        }
    }

    public static String getUrl(JsonNode root) {
        if (root.get("err_code").asInt() == 0) {
            return root.get("data").get("play_url").asText();
        }
        return null;
    }

}
