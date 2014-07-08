/*
 * (c) Copyright 2001-2004 Yann-Ga�l Gu�h�neuc,
 * University of Montr�al.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package modec.solver.constraint.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since 2013/04/28
 */
public final class TestMoDecSolver extends junit.framework.TestSuite {

	public TestMoDecSolver() {
	}
	public TestMoDecSolver(final Class theClass) {
		super(theClass);
	}
	public TestMoDecSolver(final String name) {
		super(name);
	}
	public static Test suite() {
		final TestMoDecSolver suite = new TestMoDecSolver();
		suite.addTest(new TestSuite(TestCaller.class));
		suite.addTest(new TestSuite(TestMessageFollowsImmediately.class));
		return suite;
	}
}
