INSERT INTO posts (id, title, text, likes_count)
VALUES (1, 'Тестовый заголовок 1',
        'Это текст первого тестового поста. Он может содержать произвольный контент для демонстрации.', 5),
       (2, 'Важная новость 2', 'Текст второго поста, возможно, описывающий важное событие или обновление.', 12),
       (3, 'Интересный факт 3', 'Третий пост может содержать интересный факт или любопытную информацию.', 8),
       (4, 'Моё мнение 4', 'Четвёртый пост представляет собой личное мнение автора на какую-либо тему.', 3),
       (5, 'Обзор продукта 5', 'Пятый пост может быть обзором какого-либо продукта или услуги.', 15),
       (6, 'Пошаговое руководство 6',
        'Шестой пост предоставляет пошаговое руководство по выполнению определённой задачи.', 20),
       (7, 'Вопрос сообществу 7', 'Седьмой пост содержит вопрос, адресованный сообществу пользователей.', 7),
       (8, 'Объявление 8', 'Восьмой пост содержит важное объявление для пользователей.', 25),
       (9, 'Обсуждение темы 9', 'Девятый пост начинает обсуждение определённой темы.', 10),
       (10, 'Тут лежит текст длинной 150 символов',
        'Очень длинный текст из 250 символов. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident.',
        30);

INSERT INTO comments (text, post_id)
VALUES ('simple comment', 10);

INSERT INTO tags (name)
VALUES ('test_tag_1'),
       ('test_tag_2');

INSERT INTO post_tag (post_id, tag_id)
VALUES (8, (SELECT id FROM tags WHERE name = 'test_tag_1')),
       (8, (SELECT id FROM tags WHERE name = 'test_tag_2'))
