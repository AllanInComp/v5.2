/* (c) Copyright 2001 and following years, Yann-Ga�l Gu�h�neuc,
 * University of Montreal.
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
/**
 * 
 */
package ptidej.ui.layout.repository.sugiyama.horizentalLayout;

import ptidej.ui.layout.repository.sugiyama.graph.INode;

/**
 * @author mohamedkahla
 * @date 27-06-2006
 */
// TODO: Very good idea to encapsulate the solver in this class
// and to factor out common algo.
public final class PriorityHorizentalLayout extends HorizentalLayout {
	/**
	 * The Pririority programming layout method
	 */
	public PriorityHorizentalLayout(final INode[][] aMatrix) {
		super(aMatrix);
	}

	// Priority programming
	public void doHorizentalLayout() {
		// setEntitiesPositions2();

		if (super.nbLevels > 1) {
			buildBinaryGraphModel();
		}

		super.solver =
			new PriorityHorizentalSolver(
				super.binaryGraphModel,
				super.matrix,
				super.nbLevels);
		// WE SOLVE THE PROBLEM
		((PriorityHorizentalSolver) super.solver).solvePriorityHorizental();
	}

	/**
	 * 
	 */
	public double[][] getVertexGlobalOrdring() {
		return super.solver.getVertexGlobalOrdring();
	}
}
