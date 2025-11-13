INSERT INTO posts (title, text, likes_count)
VALUES ('Тестовый заголовок 1', 'Это текст первого тестового поста.', 0);

INSERT INTO comments (text, post_id)
VALUES ('Это первый комментарий к тестовому посту.', (SELECT id FROM posts LIMIT 1));
