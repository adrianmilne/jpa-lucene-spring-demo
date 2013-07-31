package com.cor.demo.jpa.manager;

import javax.persistence.EntityManager;

import mockit.Expectations;
import mockit.Mocked;

import org.hibernate.search.MassIndexer;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.junit.Before;
import org.junit.Test;

import com.cor.demo.jpa.entity.Book;

/**
 * Illustration of basic Unit Testing using JMockit and JUnit.
 */
public class BookManagerUnitTest {

    /** Book Manager Under Test. */
    private BookManager bookManager;

    @Mocked
    private EntityManager mockEntityManager;

    @Before
    public void setup() throws Exception {
        bookManager = new BookManager();
        bookManager.setEntityManager(mockEntityManager);
    }

    @Test
    public void testAddBook(@Mocked
    final Book mockBook) throws Exception {

        new Expectations() {
            {
                mockEntityManager.persist(mockBook);
            }
        };

        bookManager.addBook(mockBook);
    }

    @Test
    public void testUpdateIndex(@Mocked(methods = "getFullTextEntityManager")
    final BookManager mockManager, @Mocked
    final FullTextEntityManager mockFullTextEntityManager, @Mocked
    final MassIndexer mockIndexer) throws Exception {

        new Expectations() {
            {
                mockManager.getFullTextEntityManager();
                result = mockFullTextEntityManager;
                mockFullTextEntityManager.createIndexer();
                result = mockIndexer;
                mockIndexer.startAndWait();
            }
        };

        bookManager.updateFullTextIndex();

    }

}
