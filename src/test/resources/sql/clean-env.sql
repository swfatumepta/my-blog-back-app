-- clean domain tables
DELETE
  FROM posts
 WHERE TRUE;
-- reset sequences
ALTER SEQUENCE posts_id_seq RESTART WITH 1;
