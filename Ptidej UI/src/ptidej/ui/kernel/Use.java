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
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF YANN-GAEL GUEHENEUC IS ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package ptidej.ui.kernel;

import ptidej.ui.IVisibility;
import ptidej.ui.primitive.IPrimitive;
import ptidej.ui.primitive.IPrimitiveFactory;
import ptidej.ui.primitive.ISymbol;

public class Use extends Relationship {
	public Use(
		final IPrimitiveFactory primitiveFactory,
		final Entity origin,
		final String methodName,
		final Entity target) {

		super(primitiveFactory, 0, origin, target);
	}
	protected IPrimitive getLine() {
		return this.getPrimitiveFactory().createDottedLine(
			this.getPosition(),
			this.getDimension(),
			this.getColor());
	}
	protected ISymbol getOriginSymbol() {
		return null;
	}
	protected String getSymbol() {
		return "-u-->";
	}
	protected final ISymbol getTargetSymbol() {
		return this.getPrimitiveFactory().createArrowSymbol(
			this.findIntersectionPointWithTarget(),
			this.getDimension(),
			this.getColor());
	}
	public int getVisibilityDisplay() {
		return IVisibility.USE_DISPLAY_ELEMENTS;
	}
	public int getVisibilityName() {
		return IVisibility.USE_NAMES;
	}
}
