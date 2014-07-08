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
package ptidej.solver.test.data.pattern;

import padl.kernel.IClass;
import padl.kernel.IComposition;
import padl.motif.models.TestMotifModel;

public final class CompositionTest extends TestMotifModel {
	public static final char[] AGGREGATE = "Aggregate".toCharArray();
	public static final char[] AGGREGATED = "Aggregated".toCharArray();
	private static final char[] COMPOSITION = "composition".toCharArray();
	private static final char[] COMPOSITION_TEST = "CompositionTest"
		.toCharArray();
	private static final long serialVersionUID = 8546899923749077555L;

	public CompositionTest() {
		final IClass anAggregateClass =
			this.getFactory().createClass(
				CompositionTest.AGGREGATE,
				CompositionTest.AGGREGATE);
		final IClass anAggregatedClass =
			this.getFactory().createClass(
				CompositionTest.AGGREGATED,
				CompositionTest.AGGREGATED);
		final IComposition aComposition =
			this.getFactory().createCompositionRelationship(
				CompositionTest.COMPOSITION,
				anAggregatedClass,
				2);

		anAggregateClass.addConstituent(aComposition);

		this.addConstituent(anAggregateClass);
		this.addConstituent(anAggregatedClass);
	}
	public char[] getName() {
		return CompositionTest.COMPOSITION_TEST;
	}
}
