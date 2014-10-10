/*******************************************************************************
 * Copyright (c) 2001-2014 Yann-Ga�l Gu�h�neuc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Yann-Ga�l Gu�h�neuc and others, see in file; API and its implementation
 ******************************************************************************/
//----------------------------------------------------
// The following code was generated by CUP v0.10k
// Tue Jun 12 09:32:02 EDT 2007
//----------------------------------------------------

package padl.creator.msefile;

/** CUP generated interface containing symbol constants. */
public interface MSESymbols {
	/* terminals */
	public static final int INTEGER = 15;
	public static final int COLON = 2;
	public static final int TRUE = 8;
	public static final int STAR = 10;
	public static final int STRING = 13;
	public static final int EOF = 0;
	public static final int VALUE = 6;
	public static final int FALSE = 9;
	public static final int ID = 3;
	public static final int IDREF = 4;
	public static final int error = 1;
	public static final int RPAREN = 12;
	public static final int PRIMITIVE = 5;
	public static final int LPAREN = 11;
	public static final int PATH = 7;
	public static final int NAME = 14;

	/* non terminals */
	static final int uniqueIDCommand = 10;
	static final int valueCommand = 15;
	static final int elementNode = 2;
	static final int className = 19;
	static final int uniqueID = 12;
	static final int referenceCommand = 11;
	static final int attributeNodes = 5;
	static final int attributeNode = 4;
	static final int valueNode = 7;
	static final int $START = 0;
	static final int typeName = 18;
	static final int primitive = 9;
	static final int expression = 16;
	static final int valueNodes = 8;
	static final int primitiveCommand = 13;
	static final int attributeName = 6;
	static final int pathCommand = 17;
	static final int root = 1;
	static final int elementNodes = 3;
	static final int primitiveName = 14;
}
