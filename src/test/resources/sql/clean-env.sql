-- clean domain tables
DELETE
  FROM comments
 WHERE TRUE;
DELETE
  FROM post_tag
 WHERE TRUE;
DELETE
  FROM posts
 WHERE TRUE;
DELETE
  FROM tags
 WHERE TRUE;
-- reset sequences
ALTER SEQUENCE comments_id_seq RESTART WITH 1;
ALTER SEQUENCE posts_id_seq RESTART WITH 1;
ALTER SEQUENCE tags_id_seq RESTART WITH 1;
