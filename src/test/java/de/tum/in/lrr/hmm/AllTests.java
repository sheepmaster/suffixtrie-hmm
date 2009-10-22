package de.tum.in.lrr.hmm;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.tum.in.lrr.hmm.t9.NumberKeyTest;
import de.tum.in.lrr.hmm.t9.TextTest;
import de.tum.in.lrr.hmm.t9.WordTest;
import de.tum.in.lrr.hmm.util.ByteBufferTest;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for de.tum.in.lrr.hmm");
        //$JUnit-BEGIN$
        suite.addTestSuite(AlphabetTest.class);
        suite.addTestSuite(BatchTrainerTest.class);
        suite.addTestSuite(ModelTest.class);
        suite.addTestSuite(UpdateStrategyTest.class);

        suite.addTestSuite(NumberKeyTest.class);
        suite.addTestSuite(TextTest.class);
        suite.addTestSuite(WordTest.class);

        suite.addTestSuite(ByteBufferTest.class);
        //$JUnit-END$
        return suite;
    }

}
