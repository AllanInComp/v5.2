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
package ptidej.ui.primitive.swt;

import java.awt.Dimension;
import java.awt.Point;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import ptidej.ui.Constants;
import ptidej.ui.RGB;
import ptidej.ui.kernel.IntermediaryPoint;
import ptidej.ui.primitive.IDottedDoubleSquareLine;

/**
 * @author Mohamed Kahla
 * @date 	16-05-2006
 */
public class DottedDoubleSquareLine extends DoubleSquareLine implements
		IDottedDoubleSquareLine {

	// 23-05-2006
	// Mohamed Kahla
	private int splitter = 45;

	/**
	 * @param primitiveFactory
	 * @param origin
	 * @param dimension
	 * @param color
	 */

	DottedDoubleSquareLine(
		final Device device,
		final GC graphics,
		final Point origin,
		final Dimension dimension,
		final RGB color) {

		super(device, graphics, origin, dimension, color);
	}

	/*
	 * @see ptidej.ui.IDrawable#paint(int, int)
	 */
	public void paint(final int xOffset, final int yOffset) {
		final Point origin = this.getPosition();
		final Point destination = this.getDestination();

		this.getGraphics().setForeground(this.getSWTColor());

		// from 
		if (origin.y < destination.y) {
			for (int movingY = origin.y; movingY < destination.y
					- this.splitter; movingY += Constants.DOT_LENGTH * 2) {

				this.getGraphics().drawLine(
					origin.x + xOffset,
					movingY + yOffset,
					origin.x + xOffset,
					Math.min(movingY + Constants.DOT_LENGTH, destination.y)
							+ yOffset);
			}
		}
		else {
			for (int movingY = destination.y; movingY < origin.y
					- this.splitter; movingY += Constants.DOT_LENGTH * 2) {

				this.getGraphics().drawLine(
					destination.x + xOffset,
					movingY + yOffset,
					destination.x + xOffset,
					Math.min(movingY + Constants.DOT_LENGTH, origin.y)
							+ yOffset);
			}
		}

		if (origin.x < destination.x) {
			if (origin.y < destination.y) {
				for (int movingX = origin.x; movingX < destination.x; movingX +=
					Constants.DOT_LENGTH * 2) {

					this.getGraphics().drawLine(
						movingX + xOffset,
						destination.y - this.splitter + yOffset,
						Math.min(movingX + Constants.DOT_LENGTH, destination.x)
								+ xOffset,
						destination.y - this.splitter + yOffset);
				}
			}
			else {
				for (int movingX = origin.x; movingX < destination.x; movingX +=
					Constants.DOT_LENGTH * 2) {

					this.getGraphics().drawLine(
						movingX + xOffset,
						origin.y - this.splitter + yOffset,
						Math.min(movingX + Constants.DOT_LENGTH, destination.x)
								+ xOffset,
						origin.y - this.splitter + yOffset);
				}
			}
		}
		else {
			if (origin.y < destination.y) {
				for (int movingX = destination.x; movingX < origin.x; movingX +=
					Constants.DOT_LENGTH * 2) {

					this.getGraphics().drawLine(
						movingX + xOffset,
						destination.y - this.splitter + yOffset,
						Math.min(movingX + Constants.DOT_LENGTH, origin.x)
								+ xOffset,
						destination.y - this.splitter + yOffset);
				}
			}
			else {
				for (int movingX = destination.x; movingX < origin.x; movingX +=
					Constants.DOT_LENGTH * 2) {

					this.getGraphics().drawLine(
						movingX + xOffset,
						origin.y - this.splitter + yOffset,
						Math.min(movingX + Constants.DOT_LENGTH, origin.x)
								+ xOffset,
						origin.y - this.splitter + yOffset);
				}
			}
		}

		if (origin.y < destination.y) {
			for (int movingY = destination.y - this.splitter; movingY < destination.y; movingY +=
				Constants.DOT_LENGTH * 2) {

				this.getGraphics().drawLine(
					destination.x + xOffset,
					movingY + yOffset,
					destination.x + xOffset,
					Math.min(movingY + Constants.DOT_LENGTH, destination.y)
							+ yOffset);
			}
		}
		else {
			for (int movingY = origin.y - this.splitter; movingY < origin.y; movingY +=
				Constants.DOT_LENGTH * 2) {

				this.getGraphics().drawLine(
					origin.x + xOffset,
					movingY + yOffset,
					origin.x + xOffset,
					Math.min(movingY + Constants.DOT_LENGTH, origin.y)
							+ yOffset);
			}
		}
	}

	public void setEdgeList(final IntermediaryPoint[] someIntermediaryPoints) {
		// TODO Auto-generated method stub
	}
	public void setSplitter(final int split) {
		this.splitter = split;
	}
}
