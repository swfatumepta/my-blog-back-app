package edu.yandex.project.integration.repository.jdbc;

import edu.yandex.project.repository.jdbc.PostJdbcRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PostJdbcJdbcRepositoryIT extends AbstractJdbcRepositoryIT {

    @Autowired
    private PostJdbcRepository postJdbcRepository;


    @Test
    void findAll_emptyResult_noExceptionThrown() {
        // given
        // table 'posts' is empty
        // when
        var actualResult = postJdbcRepository.findAll("", 0, 100);
        // then
        MatcherAssert.assertThat(actualResult, IsEmptyCollection.empty());
    }
}
