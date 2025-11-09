package edu.yandex.project.repository.jdbc.util;

import edu.yandex.project.entity.PostEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostEntityPage {
    private List<PostEntity> content = new ArrayList<>();
    private int totalCount;

    private int currentPageNumber;
    private int currentPageSize;

    public PostEntityPage(List<PostEntity> content, int totalCount) {
        this.content = content;
        this.totalCount = totalCount;
    }
}
