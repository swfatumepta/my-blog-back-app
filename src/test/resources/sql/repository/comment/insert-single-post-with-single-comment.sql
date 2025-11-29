INSERT INTO posts (id, title, text, likes_count)
VALUES (777, 'Тестовый заголовок 1', 'Это текст первого тестового поста.', 0);

INSERT INTO comments (id, text, post_id)
VALUES (777, 'Это первый комментарий к тестовому посту.', 777);
