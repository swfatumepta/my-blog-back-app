package edu.yandex.project.repository;

import edu.yandex.project.entity.PostEntity;
import edu.yandex.project.entity.TagEntity;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TagRepository {

    /**
     * Добавление тегов к посту: несуществующие теги будут предварительно сохранены в БД, существующие останутся неизменными
     * <p>
     * @param postId        идентфиикатор {@link PostEntity}
     * @param tagsToBeAdded список наименований тегов ({@link TagEntity#getName()}) для привязки к посту
     * @return список уникальных тегов, привязанных к посту с полной информацией по ним
     */
    List<TagEntity> createPostTags(@NonNull Long postId, @NonNull List<String> tagsToBeAdded);

    List<TagEntity> findAllByPostId(@NonNull Long postId);

    /**
     * Удалить связь между переданным постом (идентификаитором) и всеми его тегами. Сами теги из БД удалены не будут
     * <p>
     * @param postId идентификатор поста
     */
    void unlinkAllTagsFromPost(Long postId);
}
