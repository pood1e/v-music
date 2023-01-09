package me.pood1e.vmusic.server.core.request.tencent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pood1e
 */
public class TencentUtils {

    private static final String COVER_URL = "https://y.gtimg.cn/music/photo_new/T002R300x300M000%s.jpg";
    private static final String MP3FORMAT = "M500%s%s.mp3";

    public static JsonNode refreshParam(String uin, String qm) {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        ObjectNode req = JsonNodeFactory.instance.objectNode();
        root.set("req1", req);
        req.put("method", "QQConnectLogin.LoginServer");
        req.put("module", "QQLogin");
        ObjectNode param = JsonNodeFactory.instance.objectNode();
        param.put("expired_in", 7776000);
        param.put("musicid", uin);
        param.put("musickey", qm);
        req.set("param", param);
        return root;
//        String data = root.toString();
//        System.out.println(data);
//        String sign = "zzb5b676496efplhdxrk+/2tbu7ldanba92ed50c8";
//        Map<String, String> reqParams = new HashMap<>();
//        reqParams.put("sign", sign);
//        reqParams.put("data", param.toString());
//        reqParams.put("format", "json");
//        reqParams.put("inCharset", "utf-8");
//        reqParams.put("outCharset", "utf-8");
//        return reqParams;
    }

    public static JsonNode searchBody(SearchKeywordQuery query) {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        ObjectNode req = JsonNodeFactory.instance.objectNode();
        root.set("req", req);
        req.put("method", "DoSearchForQQMusicDesktop");
        req.put("module", "music.search.SearchCgiService");
        ObjectNode param = JsonNodeFactory.instance.objectNode();
        param.put("query", query.getKeyword());
        param.put("page_num", query.getPage());
        param.put("num_per_page", query.getPageSize());
        req.set("param", param);
        return root;
    }

    public static JsonNode fetchUrl(List<String> ids) {
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        ObjectNode req = JsonNodeFactory.instance.objectNode();
        root.set("req", req);
        req.put("method", "CgiGetVkey");
        req.put("module", "vkey.GetVkeyServer");
        ObjectNode param = JsonNodeFactory.instance.objectNode();
        ArrayNode musicIds = JsonNodeFactory.instance.arrayNode();
        ArrayNode filename = JsonNodeFactory.instance.arrayNode();
        ids.forEach(id -> {
            musicIds.add(id);
            filename.add(String.format(MP3FORMAT, id, id));
        });
        param.put("guid", "6227871");
        param.put("platform", "20");
        param.set("songmid", musicIds);
        param.set("filename", filename);
        req.set("param", param);
        return root;
    }

    public static String getUrl(JsonNode root) {
        String host = root.get("req").get("data").get("sip").get(0).asText();
        JsonNode node = root.get("req").get("data").get("midurlinfo").get(0);
        String purl = node.get("purl").asText();
        if (StringUtils.hasText(purl)) {
            return host + purl;
        }
        return null;
    }

    public static void parseUrlResult(JsonNode root, List<Song> songs) {
        Map<String, Song> songMap = songs.stream().collect(Collectors.toMap(Song::getMid, v -> v));
        JsonNode sip = root.get("req").get("data").get("sip");
        if (sip.size() == 0) {
            return;
        }
        String host = sip.get(0).asText();
        root.get("req").get("data").get("midurlinfo").forEach(node -> {
            Song song = songMap.get(node.get("songmid").asText());
            String purl = node.get("purl").asText();
            if (StringUtils.hasText(purl) && song != null) {
                song.setUrl(host + purl);
            }
        });
    }

    public static List<Song> parseSearchResult(JsonNode root) {
        ArrayNode songs = (ArrayNode) root.get("req").get("data").get("body").get("song").get("list");
        List<Song> searchedSongs = new ArrayList<>();
        for (JsonNode songJson : songs) {
            if (songJson.get("pay").get("pay_play").asBoolean()) {
                continue;
            }
            ArrayNode singer = (ArrayNode) songJson.get("singer");
            Set<String> singers = new HashSet<>();
            singer.forEach(node -> singers.add(node.get("name").asText()));
            Song defaultSong = Song.builder()
                    .mid(songJson.get("mid").asText())
                    .source(OutSource.TENCENT)
                    .name(songJson.get("title").asText())
                    .authors(singers)
                    .duration(songJson.get("interval").asInt())
                    .cover(String.format(COVER_URL, songJson.get("album").get("mid").asText()))
                    .build();
            searchedSongs.add(defaultSong);
        }
        return searchedSongs;
    }
}
