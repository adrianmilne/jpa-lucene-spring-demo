package com.cor.demo.jpa.manager;

import java.io.InputStream;
import java.util.List;

import org.dbunit.DBTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.impl.SessionImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cor.demo.jpa.entity.Book;
import com.cor.demo.jpa.entity.BookCategory;

/**
 * DBUnit Test - loads data defined in 'test-data-set.xml' into the database to run tests against the
 * BookManager. More thorough (and ultimately easier in this context) than using mocks.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context.xml" })
public class BookManagerDBUnitTest extends DBTestCase {

    /** Logger. */
    private static Logger LOG = LoggerFactory.getLogger(BookManagerDBUnitTest.class);

    /** Book Manager Under Test. */
    @Autowired
    private BookManager bookManager;

    @Before
    public void setup() throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getDatabaseConnection(), getDataSet());
    }

    @After
    public void tearDown() {
        deleteBooks();
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test-data-set.xml");
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        return builder.build(inputStream);
    }

    /**
     * Get the underlying database connection from the JPA Entity Manager (DBUnit needs this connection).
     * @return Database Connection
     * @throws Exception
     */
    private IDatabaseConnection getDatabaseConnection() throws Exception {
        return new DatabaseConnection(((SessionImpl) (bookManager.getEntityManager().getDelegate())).connection());
    }

    /**
     * Tests the expected results for searching for 'Space' in SCF-FI books.
     */
    @Test
    public void testSciFiBookSearch() throws Exception {

        bookManager.listAllBooks();
        bookManager.updateFullTextIndex();
        List<Book> results = bookManager.search(BookCategory.SCIFI, "Space");

        assertEquals("Expected 2 results for SCI FI search for 'Space'", 2, results.size());
        assertEquals("Expected 1st result to be '2001: A Space Oddysey'", "2001: A Space Oddysey", results.get(0).getTitle());
        assertEquals("Expected 2nd result to be 'Apollo 13'", "Apollo 13", results.get(1).getTitle());
    }

    private void deleteBooks() {
        LOG.info("Deleting Books...-");
        bookManager.deleteAllBooks();
    }

}
