package me.pood1e.vmusic.server.core.request.migu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pood1e
 */
public class MiguUtils {

    public static Map<String, String> fetchUrlBody(String id) {
        Map<String, String> map = new HashMap<>();
        map.put("copyrightId", id);
        map.put("resourceType", "2");
        return map;
    }

    public static void parseUrl(JsonNode root, Song song) {
        JsonNode resource = root.get("resource");
        if (resource.size() == 0) {
            return;
        }
        ArrayNode formats = (ArrayNode) resource.get(0).get("rateFormats");
        String url = null;
        for (JsonNode node : formats) {
            if ("2".equals(node.get("resourceType").asText())) {
                url = node.get("url").asText().replaceAll("ftp://[^/]+", "https://freetyst.nf.migu.cn");
                url = URLDecoder.decode(url, StandardCharsets.UTF_8);
                break;
            }
        }
        song.setUrl(url);
    }

    public static Map<String, String> searchBody(SearchKeywordQuery query) {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", query.getKeyword());
        map.put("pgc", String.valueOf(query.getPage()));
        map.put("rows", String.valueOf(query.getPageSize()));
        map.put("type", "2");
        return map;
    }

    public static String getUrl(JsonNode node) {
        String url = node.get("data").get("listenUrl").asText();
        if (StringUtils.hasText(url)) {
            return url;
        }
        return null;
    }

    public static List<Song> parseSearchResult(JsonNode root) {
        List<Song> songs = new ArrayList<>();
        if (root.get("musics") == null) {
            return songs;
        }
        root.get("musics").forEach(node -> {
            String url = node.get("mp3").isNull() ? null : URLDecoder.decode(node.get("mp3").asText(), StandardCharsets.UTF_8);
            if (url != null) {
                url = url.replaceAll("\\+", "%2B");
            }
            Set<String> singers = Set.of(node.get("singerName").asText().split(","));
            songs.add(Song.builder()
                    .mid(node.get("copyrightId").asText())
                    .name(node.get("songName").asText())
                    .source(OutSource.MIGU)
                    .authors(singers)
                    .cover(node.get("cover").asText())
                    .url(url)
                    .build());
        });
        return songs;
    }

    public static Map<String, String> fetchDuration(List<String> ids) {
        Map<String, String> map = new HashMap<>();
        map.put("type", "1");
        map.put("copyrightId", String.join(",", ids));
        return map;
    }

    public static void parseDurationResult(JsonNode root, List<Song> songs) {
        Map<String, Song> songMap = songs.stream().collect(Collectors.toMap(Song::getMid, v -> v));
        root.get("items").forEach(node -> {
            Song song = songMap.get(node.get("copyrightId").asText());
            if (song == null) {
                return;
            }
            song.setDuration(formatDuration(node.get("length").asText()));
        });
    }

    public static int formatDuration(String time) {
        try {
            return (int) Duration.between(LocalTime.MIN, LocalTime.parse(time)).toSeconds();
        } catch (DateTimeParseException e) {
            return 0;
        }
    }
}
