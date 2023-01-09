package me.pood1e.vmusic.server.core.model.data;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author pood1e
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "playlist")
public class PlaylistDoc implements BaseIdentify {
    private String id;
    private String name;
}
