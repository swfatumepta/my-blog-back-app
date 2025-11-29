package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.integration.AbstractDbIT;
import edu.yandex.project.repository.jdbc.CommentJdbcRepository;
import edu.yandex.project.repository.jdbc.PostJdbcRepository;
import edu.yandex.project.repository.jdbc.TagJdbcRepository;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJdbcTest
@Import({
        CommentJdbcRepository.class,
        PostJdbcRepository.class,
        TagJdbcRepository.class
})
public class AbstractJdbcRepositoryIT extends AbstractDbIT {
}
