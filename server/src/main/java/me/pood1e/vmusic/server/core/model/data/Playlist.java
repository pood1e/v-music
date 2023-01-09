package me.pood1e.vmusic.server.core.model.data;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author pood1e
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    private String id;
    private String name;
    private OutSource source;
    private List<Song> songs;
}
