INSERT INTO posts (id, title, text, likes_count)
VALUES (999, 'Тестовый заголовок 1', 'Это текст первого тестового поста.', 42);

INSERT INTO comments (text, post_id)
VALUES ('simple comment', 999);

INSERT INTO tags (id, name)
VALUES (777, 'test_tag_1'),
       (888, 'test_tag_2');

INSERT INTO post_tag (post_id, tag_id)
VALUES (999, 777),
       (999, 888)
