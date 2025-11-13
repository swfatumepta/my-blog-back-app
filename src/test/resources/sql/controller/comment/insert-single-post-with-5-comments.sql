INSERT INTO posts (title, text, likes_count)
VALUES ('Тестовый заголовок 1', 'Это текст первого тестового поста.', 42);

INSERT INTO comments (text, post_id)
VALUES ('Это первый комментарий к тестовому посту.', (SELECT id FROM posts LIMIT 1)),
       ('Отличная статья! Спасибо за информацию.', (SELECT id FROM posts LIMIT 1)),
       ('Как можно это применить на практике?', (SELECT id FROM posts LIMIT 1)),
       ('Согласен с автором, всё логично и понятно изложено.', (SELECT id FROM posts LIMIT 1)),
       ('Буду ждать продолжения.', (SELECT id FROM posts LIMIT 1));

