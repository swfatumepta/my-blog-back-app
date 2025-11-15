package edu.yandex.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity {
    private Long id;

    private String title;
    private String text;
    private Integer likesCount;
    private Integer commentsCount;
    private LocalDateTime createdAt;

    public PostEntity(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public PostEntity(Long id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }
}
