/*
 * Writen in CLAIRE by
 * @author Yann-Ga�l Gu�h�neuc
 * Translated and adapted from CLAIRE version to JAVA by
 * @author Iyadh Sidhom
 * @author Salim Bensemmane
 * @author Fay�al Skhiri
 *
 * (c) Copyright 2000-2004 Yann-Ga�l Gu�h�neuc,
 */
package ptidej.solver;

import java.util.ArrayList;

import choco.AbstractConstraint;
import choco.palm.explain.Explanation;
import choco.palm.search.PalmRepair;


public abstract class Repair extends PalmRepair {
	private Problem problem;

	public Repair(final Problem aProblem) {
		this.problem = aProblem;
	}

	public abstract void remove(final AbstractConstraint aConstraint);
	public abstract ArrayList[] ptidejSelectDecisionToUndo(final Explanation anExplanation);
	public Problem getProblem() {
		return this.problem;
	}
}
