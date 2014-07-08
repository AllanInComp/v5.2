package ptidej.test.all;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2008/12/04
 */
public final class TestAllPtidej extends TestSuite {
	public static Test suite() {
		final TestAllPtidej suite = new TestAllPtidej();

		suite.addTest(TestAllCreators.suite());
		suite.addTest(TestAllPtidejButCreators.suite());

		return suite;
	}
}
