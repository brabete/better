package io.github.better.application.repository.search;

import io.github.better.application.domain.TODOList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TODOList} entity.
 */
public interface TODOListSearchRepository extends ElasticsearchRepository<TODOList, Long> {
}
