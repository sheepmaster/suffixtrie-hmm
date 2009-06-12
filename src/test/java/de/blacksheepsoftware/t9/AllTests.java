package de.blacksheepsoftware.t9;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for de.blacksheepsoftware.t9");
        //$JUnit-BEGIN$
        suite.addTestSuite(ModelTest.class);
        suite.addTestSuite(TextTest.class);
        suite.addTestSuite(NumberKeyTest.class);
        suite.addTestSuite(WordTest.class);
        suite.addTestSuite(UpdateStrategyTest.class);
        //$JUnit-END$
        return suite;
    }

}
