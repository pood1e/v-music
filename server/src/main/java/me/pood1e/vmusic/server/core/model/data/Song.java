package me.pood1e.vmusic.server.core.model.data;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * @author pood1e
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "song")
public class Song implements BaseIdentify {
    private String id;
    private String mid;
    private OutSource source;
    private String name;
    private Set<String> authors;
    private int duration;
    private String cover;
    private String url;
    @Builder.Default
    private SyncState state = SyncState.ORIGIN;
}
