INSERT INTO posts (title, text, likes_count)
VALUES ('Тестовый заголовок 1', 'Это текст первого тестового поста.', 42);

INSERT INTO comments (text, post_id)
VALUES ('simple comment', (SELECT id FROM posts LIMIT 1));
