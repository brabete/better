package io.github.better.application.web.rest;

import io.github.better.application.BetterApp;
import io.github.better.application.domain.TODOList;
import io.github.better.application.repository.TODOListRepository;
import io.github.better.application.repository.search.TODOListSearchRepository;
import io.github.better.application.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static io.github.better.application.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link TODOListResource} REST controller.
 */
@SpringBootTest(classes = BetterApp.class)
public class TODOListResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_CREATED = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private TODOListRepository tODOListRepository;

    /**
     * This repository is mocked in the io.github.better.application.repository.search test package.
     *
     * @see io.github.better.application.repository.search.TODOListSearchRepositoryMockConfiguration
     */
    @Autowired
    private TODOListSearchRepository mockTODOListSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restTODOListMockMvc;

    private TODOList tODOList;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TODOListResource tODOListResource = new TODOListResource(tODOListRepository, mockTODOListSearchRepository);
        this.restTODOListMockMvc = MockMvcBuilders.standaloneSetup(tODOListResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TODOList createEntity(EntityManager em) {
        TODOList tODOList = new TODOList()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .dateCreated(DEFAULT_DATE_CREATED);
        return tODOList;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TODOList createUpdatedEntity(EntityManager em) {
        TODOList tODOList = new TODOList()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .dateCreated(UPDATED_DATE_CREATED);
        return tODOList;
    }

    @BeforeEach
    public void initTest() {
        tODOList = createEntity(em);
    }

    @Test
    @Transactional
    public void createTODOList() throws Exception {
        int databaseSizeBeforeCreate = tODOListRepository.findAll().size();

        // Create the TODOList
        restTODOListMockMvc.perform(post("/api/todo-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tODOList)))
            .andExpect(status().isCreated());

        // Validate the TODOList in the database
        List<TODOList> tODOListList = tODOListRepository.findAll();
        assertThat(tODOListList).hasSize(databaseSizeBeforeCreate + 1);
        TODOList testTODOList = tODOListList.get(tODOListList.size() - 1);
        assertThat(testTODOList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTODOList.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTODOList.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);

        // Validate the TODOList in Elasticsearch
        verify(mockTODOListSearchRepository, times(1)).save(testTODOList);
    }

    @Test
    @Transactional
    public void createTODOListWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tODOListRepository.findAll().size();

        // Create the TODOList with an existing ID
        tODOList.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTODOListMockMvc.perform(post("/api/todo-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tODOList)))
            .andExpect(status().isBadRequest());

        // Validate the TODOList in the database
        List<TODOList> tODOListList = tODOListRepository.findAll();
        assertThat(tODOListList).hasSize(databaseSizeBeforeCreate);

        // Validate the TODOList in Elasticsearch
        verify(mockTODOListSearchRepository, times(0)).save(tODOList);
    }


    @Test
    @Transactional
    public void getAllTODOLists() throws Exception {
        // Initialize the database
        tODOListRepository.saveAndFlush(tODOList);

        // Get all the tODOListList
        restTODOListMockMvc.perform(get("/api/todo-lists?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tODOList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));
    }
    
    @Test
    @Transactional
    public void getTODOList() throws Exception {
        // Initialize the database
        tODOListRepository.saveAndFlush(tODOList);

        // Get the tODOList
        restTODOListMockMvc.perform(get("/api/todo-lists/{id}", tODOList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tODOList.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTODOList() throws Exception {
        // Get the tODOList
        restTODOListMockMvc.perform(get("/api/todo-lists/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTODOList() throws Exception {
        // Initialize the database
        tODOListRepository.saveAndFlush(tODOList);

        int databaseSizeBeforeUpdate = tODOListRepository.findAll().size();

        // Update the tODOList
        TODOList updatedTODOList = tODOListRepository.findById(tODOList.getId()).get();
        // Disconnect from session so that the updates on updatedTODOList are not directly saved in db
        em.detach(updatedTODOList);
        updatedTODOList
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .dateCreated(UPDATED_DATE_CREATED);

        restTODOListMockMvc.perform(put("/api/todo-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTODOList)))
            .andExpect(status().isOk());

        // Validate the TODOList in the database
        List<TODOList> tODOListList = tODOListRepository.findAll();
        assertThat(tODOListList).hasSize(databaseSizeBeforeUpdate);
        TODOList testTODOList = tODOListList.get(tODOListList.size() - 1);
        assertThat(testTODOList.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTODOList.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTODOList.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);

        // Validate the TODOList in Elasticsearch
        verify(mockTODOListSearchRepository, times(1)).save(testTODOList);
    }

    @Test
    @Transactional
    public void updateNonExistingTODOList() throws Exception {
        int databaseSizeBeforeUpdate = tODOListRepository.findAll().size();

        // Create the TODOList

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTODOListMockMvc.perform(put("/api/todo-lists")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tODOList)))
            .andExpect(status().isBadRequest());

        // Validate the TODOList in the database
        List<TODOList> tODOListList = tODOListRepository.findAll();
        assertThat(tODOListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TODOList in Elasticsearch
        verify(mockTODOListSearchRepository, times(0)).save(tODOList);
    }

    @Test
    @Transactional
    public void deleteTODOList() throws Exception {
        // Initialize the database
        tODOListRepository.saveAndFlush(tODOList);

        int databaseSizeBeforeDelete = tODOListRepository.findAll().size();

        // Delete the tODOList
        restTODOListMockMvc.perform(delete("/api/todo-lists/{id}", tODOList.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<TODOList> tODOListList = tODOListRepository.findAll();
        assertThat(tODOListList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TODOList in Elasticsearch
        verify(mockTODOListSearchRepository, times(1)).deleteById(tODOList.getId());
    }

    @Test
    @Transactional
    public void searchTODOList() throws Exception {
        // Initialize the database
        tODOListRepository.saveAndFlush(tODOList);
        when(mockTODOListSearchRepository.search(queryStringQuery("id:" + tODOList.getId())))
            .thenReturn(Collections.singletonList(tODOList));
        // Search the tODOList
        restTODOListMockMvc.perform(get("/api/_search/todo-lists?query=id:" + tODOList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tODOList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TODOList.class);
        TODOList tODOList1 = new TODOList();
        tODOList1.setId(1L);
        TODOList tODOList2 = new TODOList();
        tODOList2.setId(tODOList1.getId());
        assertThat(tODOList1).isEqualTo(tODOList2);
        tODOList2.setId(2L);
        assertThat(tODOList1).isNotEqualTo(tODOList2);
        tODOList1.setId(null);
        assertThat(tODOList1).isNotEqualTo(tODOList2);
    }
}
