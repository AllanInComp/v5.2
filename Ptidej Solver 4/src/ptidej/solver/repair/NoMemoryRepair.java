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
package ptidej.solver.repair;

import ptidej.solver.Problem;
import ptidej.solver.Repair;
import choco.AbstractConstraint;

public abstract class NoMemoryRepair extends Repair {
	public NoMemoryRepair(final Problem problem) {
		super(problem);
	}
	public final void remove(final AbstractConstraint constraint) {
		//we don't use the remove in noMemoryRepair
	}
}
