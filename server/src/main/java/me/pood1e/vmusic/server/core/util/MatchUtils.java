package me.pood1e.vmusic.server.core.util;

import me.pood1e.vmusic.server.core.model.data.Song;

import java.util.Objects;

/**
 * @author pood1e
 */
public class MatchUtils {

    public static boolean match(Song except, Song actual) {
        return Objects.equals(except.getName(), actual.getName()) &&
                except.getAuthors().containsAll(actual.getAuthors()) &&
                except.getAuthors().size() == actual.getAuthors().size();
    }

}
