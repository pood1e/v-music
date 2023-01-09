package me.pood1e.vmusic.server.core.request.netease;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.pood1e.vmusic.server.core.model.data.OutSource;
import me.pood1e.vmusic.server.core.model.data.Playlist;
import me.pood1e.vmusic.server.core.model.data.Song;
import me.pood1e.vmusic.server.core.model.query.SearchKeywordQuery;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pood1e
 */
public class NeteaseUtils {

    private final static String BASE_62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final static String AES_KEY = "0CoJUm6Qyw8W8jud";
    private final static String AES_IV = "0102030405060708";
    private final static String NETEASE_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgtQn2JZ34ZC28NWYpAUd98iZ37BUrX/aKzmFbt7clFSs6sXqHauqKWqdtLkF2KexO40H1YTX8z2lSgBBOAxLsvaklV8k4cBFK9snQXE9/DDaFt6Rr7iVZMldczhC0JNgTz+SHXT6CBHuX3e9SdB1Ua44oncaTWz7OBGLbCiK45wIDAQAB";

    public static String searchBody(SearchKeywordQuery query) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("s", query.getKeyword());
        node.put("limit", query.getPageSize());
        node.put("type", 1);
        node.put("offset", query.getPage());
        return encrypt(node.toString());
    }

    public static String fetchUrlBody(List<String> ids) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        ids.forEach(id -> {
            ObjectNode idNode = JsonNodeFactory.instance.objectNode();
            idNode.put("id", Long.valueOf(id));
            arrayNode.add(idNode);
        });

        node.put("c", arrayNode.toString());
        node.put("csrf_token", "0");
        return encrypt(node.toString());
    }

    public static String playlistBody(String id) {
        return "id=" + id + "&n=100000&s=0";
    }

    public static Playlist parsePlaylist(JsonNode root) {
        List<Song> songs = new ArrayList<>();
        root.get("playlist").get("trackIds").forEach(node -> songs.add(Song.builder()
                .mid(node.get("id").asText())
                .build()));
        return Playlist.builder()
                .name(root.get("playlist").get("name").asText())
                .id(root.get("playlist").get("id").asText())
                .source(OutSource.NETEASE)
                .songs(songs)
                .build();
    }

    public static void fillDetailInfo(JsonNode root, List<Song> songs) {
        parseDetailInfo(root, songs, false, true);
    }

    public static String getUrl(JsonNode root) {
        JsonNode songNode = root.get("songs").get(0);
        String id = songNode.get("id").asText();
        JsonNode privilegeNode = root.get("privileges").get(0);
        int fee = privilegeNode.get("fee").asInt();
        int st = privilegeNode.get("st").asInt();
        int cp = privilegeNode.get("cp").asInt();
        boolean can = st == 0 && cp == 1 && (fee == 0 || fee == 8);
        if (can) {
            return "https://music.163.com/song/media/outer/url?id=" + id;
        }
        return null;
    }

    public static void fillUrlInfo(JsonNode root, List<Song> songs) {
        parseDetailInfo(root, songs, true, false);
    }

    private static void parseDetailInfo(JsonNode root, List<Song> songs, boolean delete, boolean basicInfo) {
        Map<String, Song> songMap = songs.stream().collect(Collectors.toMap(Song::getMid, v -> v));
        JsonNode songsNode = root.get("songs");
        JsonNode privilegesNode = root.get("privileges");
        for (int i = 0; i < songsNode.size(); i++) {
            JsonNode songNode = songsNode.get(i);
            String id = songNode.get("id").asText();
            Song song = songMap.get(id);
            if (song == null) {
                continue;
            }
            if (basicInfo) {
                song.setSource(OutSource.NETEASE);
                song.setName(songNode.get("name").asText());
                Set<String> singer = new HashSet<>();
                songNode.get("ar").forEach(node -> singer.add(node.get("name").asText()));
                song.setAuthors(singer);
                song.setDuration(songNode.get("dt").asInt() / 1000);
                song.setCover(songNode.get("al").get("picUrl").asText());
            }
            JsonNode privilegeNode = privilegesNode.get(i);
            int fee = privilegeNode.get("fee").asInt();
            int st = privilegeNode.get("st").asInt();
            int cp = privilegeNode.get("cp").asInt();
            boolean can = st == 0 && cp == 1 && (fee == 0 || fee == 8);
            if (can) {
                song.setCover(songNode.get("al").get("picUrl").asText());
                song.setUrl("https://music.163.com/song/media/outer/url?id=" + id);
            } else if (delete) {
                songs.remove(song);
            }
        }
    }

    private static String encrypt(String payload) {
        try {
            String key = randomKey();
            String aesRet = aesEncrypt(payload, key);
            String revKey = new StringBuilder(key).reverse().toString();
            String rsaRet = rsaEncrypt(revKey);
            return "params=" + URLEncoder.encode(aesRet, StandardCharsets.UTF_8) + "&encSecKey=" + URLEncoder.encode(rsaRet, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Song> parseSearchResult(JsonNode root) {
        List<Song> songs = new ArrayList<>();
        ArrayNode songJson = (ArrayNode) root.get("result").get("songs");
        songJson.forEach(node -> {
            int fee = node.get("fee").asInt();
            if (fee != 0 && fee != 8) {
                return;
            }
            Set<String> singers = new HashSet<>();
            node.get("artists").forEach(singer -> singers.add(singer.get("name").asText()));
            songs.add(Song.builder()
                    .name(node.get("name").asText())
                    .source(OutSource.NETEASE)
                    .mid(node.get("id").asText())
                    .authors(singers)
                    .duration(node.get("duration").asInt() / 1000)
                    .build());
        });
        return songs;
    }

    private static String aesEncrypt(String data, String key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(AES_IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        String ret1 = Base64.getEncoder().encodeToString(encrypted);
        SecretKeySpec keySpec2 = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec2, iv);
        byte[] encrypted2 = cipher.doFinal(ret1.getBytes());
        return Base64.getEncoder().encodeToString(encrypted2);
    }

    private static String rsaEncrypt(String data) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(NETEASE_PUBLIC_KEY);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = factory.generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return HexFormat.of().formatHex(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8))).toUpperCase(Locale.ROOT);
    }

    private static String randomKey() {
        byte[] x = new byte[16];
        new Random().nextBytes(x);
        char[] res = new char[16];
        for (int i = 0; i < x.length; i++) {
            res[i] = BASE_62.charAt(Math.abs(x[i] % 62));
        }
        return String.valueOf(res);
    }

}
