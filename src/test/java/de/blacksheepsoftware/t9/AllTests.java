package de.blacksheepsoftware.t9;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.blacksheepsoftware.hmm.AlphabetTest;
import de.blacksheepsoftware.hmm.BatchTrainerTest;
import de.blacksheepsoftware.hmm.ModelTest;
import de.blacksheepsoftware.hmm.UpdateStrategyTest;
import de.blacksheepsoftware.util.IntArrayTest;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for de.blacksheepsoftware.t9");
        //$JUnit-BEGIN$
        suite.addTestSuite(AlphabetTest.class);
        suite.addTestSuite(BatchTrainerTest.class);
        suite.addTestSuite(ModelTest.class);
        suite.addTestSuite(UpdateStrategyTest.class);

        suite.addTestSuite(NumberKeyTest.class);
        suite.addTestSuite(TextTest.class);
        suite.addTestSuite(WordTest.class);

        suite.addTestSuite(IntArrayTest.class);
        //$JUnit-END$
        return suite;
    }

}
