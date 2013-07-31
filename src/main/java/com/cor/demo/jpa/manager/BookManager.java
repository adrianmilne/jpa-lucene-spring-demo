package com.cor.demo.jpa.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cor.demo.jpa.entity.Book;
import com.cor.demo.jpa.entity.BookCategory;

/**
 * Manager for persisting and searching on Books. Uses JPA and Lucene.
 */
@Component
@Scope(value = "singleton")
public class BookManager {

    /** Logger. */
    private static Logger LOG = LoggerFactory.getLogger(BookManager.class);

    /** JPA Persistence Unit. */
    @PersistenceContext(type = PersistenceContextType.EXTENDED, name = "booksPU")
    private EntityManager em;

    /** Hibernate Full Text Entity Manager. */
    private FullTextEntityManager ftem;

    /**
     * Method to manually update the Full Text Index. This is not required if inserting entities
     * using this Manager as they will automatically be indexed. Useful though if you need to index
     * data inserted using a different method (e.g. pre-existing data, or test data inserted via
     * scripts or DbUnit).
     */
    public void updateFullTextIndex() throws Exception {
        LOG.info("Updating Index");
        getFullTextEntityManager().createIndexer().startAndWait();
    }

    /**
     * Add a Book to the Database.
     */
    @Transactional
    public Book addBook(Book book) {
        LOG.info("Adding Book : " + book);
        em.persist(book);
        return book;
    }

    /**
     * Delete All Books.
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public void deleteAllBooks() {

        LOG.info("Delete All Books");

        Query allBooks = em.createQuery("select b from Book b");
        List<Book> books = allBooks.getResultList();

        // We need to delete individually (rather than a bulk delete) to ensure they are removed
        // from the Lucene index correctly
        for (Book b : books) {
            em.remove(b);
        }

    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void listAllBooks() {

        LOG.info("List All Books");
        LOG.info("------------------------------------------");

        Query allBooks = em.createQuery("select b from Book b");
        List<Book> books = allBooks.getResultList();

        for (Book b : books) {
            LOG.info(b.toString());
            getFullTextEntityManager().index(b);
        }

    }

    /**
     * Search for a Book.
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public List<Book> search(BookCategory category, String searchString) {

        LOG.info("------------------------------------------");
        LOG.info("Searching Books in category '" + category + "' for phrase '" + searchString + "'");

        // Create a Query Builder
        QueryBuilder qb = getFullTextEntityManager().getSearchFactory().buildQueryBuilder().forEntity(Book.class).get();

        // Create a Lucene Full Text Query
        org.apache.lucene.search.Query luceneQuery = qb.bool()
                .must(qb.keyword().onFields("title", "description").matching(searchString).createQuery())
                .must(qb.keyword().onField("category").matching(category).createQuery()).createQuery();

        Query fullTextQuery = getFullTextEntityManager().createFullTextQuery(luceneQuery, Book.class);

        // Run Query and print out results to console
        List<Book> result = (List<Book>) fullTextQuery.getResultList();

        // Log the Results
        LOG.info("Found Matching Books :" + result.size());
        for (Book b : result) {
            LOG.info(" - " + b);
        }

        return result;
    }

    /**
     * Convenience method to get Full Test Entity Manager. Protected scope to assist mocking in Unit
     * Tests.
     * @return Full Text Entity Manager.
     */
    protected FullTextEntityManager getFullTextEntityManager() {
        if (ftem == null) {
            ftem = Search.getFullTextEntityManager(em);
        }
        return ftem;
    }

    /**
     * Get the JPA Entity Manager (required for the DBUnit Tests).
     * @return Entity manager
     */
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Sets the JPA Entity Manager (required to assist with mocking in Unit Test)
     * @param em EntityManager
     */
    protected void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
