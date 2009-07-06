package de.blacksheepsoftware.t9;

import de.blacksheepsoftware.hmm.BatchTrainerTest;
import de.blacksheepsoftware.hmm.ModelTest;
import de.blacksheepsoftware.hmm.UpdateStrategyTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for de.blacksheepsoftware.t9");
        //$JUnit-BEGIN$
        suite.addTestSuite(BatchTrainerTest.class);
        suite.addTestSuite(ModelTest.class);
        suite.addTestSuite(TextTest.class);
        suite.addTestSuite(NumberKeyTest.class);
        suite.addTestSuite(UpdateStrategyTest.class);
        suite.addTestSuite(WordTest.class);
        //$JUnit-END$
        return suite;
    }

}
