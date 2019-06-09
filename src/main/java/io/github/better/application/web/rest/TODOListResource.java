package io.github.better.application.web.rest;

import io.github.better.application.domain.TODOList;
import io.github.better.application.repository.TODOListRepository;
import io.github.better.application.repository.search.TODOListSearchRepository;
import io.github.better.application.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link io.github.better.application.domain.TODOList}.
 */
@RestController
@RequestMapping("/api")
public class TODOListResource {

    private final Logger log = LoggerFactory.getLogger(TODOListResource.class);

    private static final String ENTITY_NAME = "tODOList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TODOListRepository tODOListRepository;

    private final TODOListSearchRepository tODOListSearchRepository;

    public TODOListResource(TODOListRepository tODOListRepository, TODOListSearchRepository tODOListSearchRepository) {
        this.tODOListRepository = tODOListRepository;
        this.tODOListSearchRepository = tODOListSearchRepository;
    }

    /**
     * {@code POST  /todo-lists} : Create a new tODOList.
     *
     * @param tODOList the tODOList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tODOList, or with status {@code 400 (Bad Request)} if the tODOList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/todo-lists")
    public ResponseEntity<TODOList> createTODOList(@RequestBody TODOList tODOList) throws URISyntaxException {
        log.debug("REST request to save TODOList : {}", tODOList);
        if (tODOList.getId() != null) {
            throw new BadRequestAlertException("A new tODOList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TODOList result = tODOListRepository.save(tODOList);
        tODOListSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/todo-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /todo-lists} : Updates an existing tODOList.
     *
     * @param tODOList the tODOList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tODOList,
     * or with status {@code 400 (Bad Request)} if the tODOList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tODOList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/todo-lists")
    public ResponseEntity<TODOList> updateTODOList(@RequestBody TODOList tODOList) throws URISyntaxException {
        log.debug("REST request to update TODOList : {}", tODOList);
        if (tODOList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TODOList result = tODOListRepository.save(tODOList);
        tODOListSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, tODOList.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /todo-lists} : get all the tODOLists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tODOLists in body.
     */
    @GetMapping("/todo-lists")
    public List<TODOList> getAllTODOLists() {
        log.debug("REST request to get all TODOLists");
        return tODOListRepository.findAll();
    }

    /**
     * {@code GET  /todo-lists/:id} : get the "id" tODOList.
     *
     * @param id the id of the tODOList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tODOList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/todo-lists/{id}")
    public ResponseEntity<TODOList> getTODOList(@PathVariable Long id) {
        log.debug("REST request to get TODOList : {}", id);
        Optional<TODOList> tODOList = tODOListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tODOList);
    }

    /**
     * {@code DELETE  /todo-lists/:id} : delete the "id" tODOList.
     *
     * @param id the id of the tODOList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/todo-lists/{id}")
    public ResponseEntity<Void> deleteTODOList(@PathVariable Long id) {
        log.debug("REST request to delete TODOList : {}", id);
        tODOListRepository.deleteById(id);
        tODOListSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/todo-lists?query=:query} : search for the tODOList corresponding
     * to the query.
     *
     * @param query the query of the tODOList search.
     * @return the result of the search.
     */
    @GetMapping("/_search/todo-lists")
    public List<TODOList> searchTODOLists(@RequestParam String query) {
        log.debug("REST request to search TODOLists for query {}", query);
        return StreamSupport
            .stream(tODOListSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
