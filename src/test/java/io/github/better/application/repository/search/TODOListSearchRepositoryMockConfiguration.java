package io.github.better.application.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link TODOListSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class TODOListSearchRepositoryMockConfiguration {

    @MockBean
    private TODOListSearchRepository mockTODOListSearchRepository;

}
