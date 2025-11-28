package edu.yandex.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {
    private Long id;

    private String name;
    private LocalDateTime createdAt;
}
