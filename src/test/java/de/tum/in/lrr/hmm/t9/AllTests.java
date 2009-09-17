package de.tum.in.lrr.hmm.t9;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.tum.in.lrr.hmm.AlphabetTest;
import de.tum.in.lrr.hmm.BatchTrainerTest;
import de.tum.in.lrr.hmm.ModelTest;
import de.tum.in.lrr.hmm.UpdateStrategyTest;
import de.tum.in.lrr.hmm.util.ByteArrayTest;

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

        suite.addTestSuite(ByteArrayTest.class);
        //$JUnit-END$
        return suite;
    }

}
