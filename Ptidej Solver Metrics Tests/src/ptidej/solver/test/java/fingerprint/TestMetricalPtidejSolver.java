/*
 * (c) Copyright 2001, 2002 Yann-Ga�l Gu�h�neuc,
 * Ecole des Mines de Nantes and Object Technology International, Inc.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * Yann-Ga�l Gu�h�neuc, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR B PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF YANN-GAEL GUEHENEUC IS ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package ptidej.solver.test.java.fingerprint;

import junit.framework.Test;

public final class TestMetricalPtidejSolver extends junit.framework.TestSuite {
	public TestMetricalPtidejSolver() {
	}
	public TestMetricalPtidejSolver(final Class theClass) {
		super(theClass);
	}
	public TestMetricalPtidejSolver(final String name) {
		super(name);
	}
	public static Test suite() {
		final TestMetricalPtidejSolver suite = new TestMetricalPtidejSolver();
		suite.addTest(new TestMetricalPtidejSolver(Composition.class));
		suite.addTest(new TestMetricalPtidejSolver(Composition2.class));
		suite.addTest(new TestMetricalPtidejSolver(Composition3.class));
		suite.addTest(new TestMetricalPtidejSolver(Composition4.class));
		
		return suite;
	}
}
