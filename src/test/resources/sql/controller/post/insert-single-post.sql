INSERT INTO posts (title, text, likes_count)
VALUES ('Тестовый заголовок 1', 'Это текст первого тестового поста.', 42);

INSERT INTO comments (text, post_id)
VALUES ('simple comment', (SELECT id FROM posts LIMIT 1));

INSERT INTO tags (name)
VALUES ('test_tag_1'),
       ('test_tag_2');

INSERT INTO post_tag (post_id, tag_id)
VALUES ((SELECT id FROM posts LIMIT 1), (SELECT id FROM tags WHERE name = 'test_tag_1')),
       ((SELECT id FROM posts LIMIT 1), (SELECT id FROM tags WHERE name = 'test_tag_2'))
