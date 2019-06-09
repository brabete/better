package io.github.better.application.repository;

import io.github.better.application.domain.TODOList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the TODOList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TODOListRepository extends JpaRepository<TODOList, Long> {

}
