package me.pood1e.vmusic.server.core.model.query;

import lombok.*;

/**
 * @author pood1e
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchKeywordQuery {
    private String keyword;
    private int page;
    private int pageSize;
}
