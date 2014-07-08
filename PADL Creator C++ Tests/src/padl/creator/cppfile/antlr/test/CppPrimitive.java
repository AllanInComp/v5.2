/*
 * (c) Copyright 2001-2003 Yann-Ga�l Gu�h�neuc,
 * �cole des Mines de Nantes and Object Technology International, Inc.
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
package padl.creator.cppfile.antlr.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import padl.kernel.IFactory;
import padl.kernel.cpp.antlr.ICPPFactoryANTLR;
import padl.kernel.cpp.antlr.impl.CPPFactoryANTLR;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2003/12/09
 */
public abstract class CppPrimitive extends TestCase {
	private static ICPPFactoryANTLR factory =
		(ICPPFactoryANTLR) CPPFactoryANTLR.getInstance();

	public static void assertAssigable(
		final String aMessage,
		final Class anInterface,
		final Class aClass) {

		if (!anInterface.isAssignableFrom(aClass)) {
			Assert.fail(aMessage);
		}
	}
	public static void setFactory(final ICPPFactoryANTLR aFactory) {
		CppPrimitive.factory = aFactory;
	}
	public static IFactory getFactory() {
		return CppPrimitive.factory;
	}

	public CppPrimitive(final String aName) {
		super(aName);
	}
}
