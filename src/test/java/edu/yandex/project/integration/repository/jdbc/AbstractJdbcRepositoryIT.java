package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.integration.AbstractDbIT;
import edu.yandex.project.repository.jdbc.CommentJdbcRepository;
import edu.yandex.project.repository.jdbc.PostJdbcRepository;
import edu.yandex.project.repository.jdbc.TagJdbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CommentJdbcRepository.class, PostJdbcRepository.class, TagJdbcRepository.class})
@JdbcTest
public class AbstractJdbcRepositoryIT extends AbstractDbIT {
    protected final static long DEFAULT_ID = 777L;

    @Autowired
    protected CommentJdbcRepository commentJdbcRepository;
    @Autowired
    protected PostJdbcRepository postJdbcRepository;
    @Autowired
    protected TagJdbcRepository tagJdbcRepository;
}
