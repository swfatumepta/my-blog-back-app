-- insert post
INSERT INTO posts (id, title, text)
VALUES (1, '', '');
-- insert tags
INSERT INTO tags (id, name)
VALUES (1, 't1'),
       (2, 't3')
    ON CONFLICT DO NOTHING;
-- link post with tags
INSERT INTO post_tag (post_id, tag_id)
VALUES (1, 1),
       (1, 2)
