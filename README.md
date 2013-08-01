jpa-lucene-spring-demo
======================

Demo project using JPA, Lucene, Spring, DBUnit and JMockit - to persist and search for simple Book data.

This is a Maven project. To build it and run the tests, you first need to install maven (http://maven.apache.org/).

Once you have Maven installed and working, and the project cloned locally, navigate to the project root directory, and type:

'mvn clean install'

This will compile the project and run the tests. There are 2 test classes - a simple Unit test that uses JUnit and JMockit, and another test that uses DBUnit.

The DBUnit test is a good example to get a feel for how this works - it will populate the in memory HSQL database, and then use Lucene to query the data in there. The example is a simple one, but illustrates the basic concepts of how to wire up the infrastructure to integrate Lucene and JPA.
