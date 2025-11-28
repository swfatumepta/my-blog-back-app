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
public class CommentEntity {
    private Long id;
    private Long postId;

    private String text;
    private LocalDateTime createdAt;

    public CommentEntity(Long postId, String text) {
        this.postId = postId;
        this.text = text;
    }

    public CommentEntity(Long id, Long postId, String text) {
        this.id = id;
        this.postId = postId;
        this.text = text;
    }
}
