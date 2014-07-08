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
package padl.creator.classfile.test.innerclasses;

import junit.framework.Assert;
import padl.creator.classfile.test.ClassFilePrimitive;
import padl.creator.classfile.util.Utils;
import padl.kernel.Constants;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IConstituent;
import padl.kernel.IField;
import padl.kernel.IMemberClass;
import padl.kernel.IPackage;
import padl.kernel.exception.CreationException;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2006/02/08
 */
public class TestMemberClasses4 extends ClassFilePrimitive {
	private static ICodeLevelModel CodeLevelModel = null;

	public TestMemberClasses4(final String aName) {
		super(aName);
	}
	protected void setUp() throws CreationException {
		if (TestMemberClasses4.CodeLevelModel == null) {
			TestMemberClasses4.CodeLevelModel =
				ClassFilePrimitive.getFactory().createCodeLevelModel(
					"ptidej.example.innerclasses");

			final IPackage packaje =
				ClassFilePrimitive.getFactory().createPackage(
					Constants.DEFAULT_PACKAGE_ID);
			// The test below works only because, historically,
			// the ID and name of top-level entities are identical!
			// TODO: distinguish ID and name for top-level entities.
			final IClass topLevelClass =
				ClassFilePrimitive.getFactory().createClass(
					"TopLevelClass".toCharArray(),
					"TopLevelClass".toCharArray());
			// PADL works best if a member class has for 
			// ID its fully-qualified name (JVM format).
			// TODO: Remove the unwritten constraint that a member class must of its fully-qualified JVM name as ID. 
			final IMemberClass memberClass =
				ClassFilePrimitive.getFactory().createMemberClass(
					"TopLevelClass$MemberClass".toCharArray(),
					"MemberClass".toCharArray());
			final IField field =
				ClassFilePrimitive.getFactory().createField(
					"MemberClass".toCharArray(),
					"MemberClass".toCharArray(),
					"String".toCharArray(),
					Constants.CARDINALITY_ONE);

			TestMemberClasses4.CodeLevelModel.addConstituent(packaje);
			packaje.addConstituent(topLevelClass);
			topLevelClass.addConstituent(memberClass);
			topLevelClass.addConstituent(field);
		}
	}
	public void testMemberEntities() {
		//	final IConstituent constituent =
		//		((IContainer) TestMemberClasses4.CodeLevelModel
		//			.getConstituentFromID("toplevelclass".toCharArray()))
		//			.getConstituentFromName("MemberClass".toCharArray());
		final IConstituent constituent =
			Utils.searchForEntity(
				TestMemberClasses4.CodeLevelModel,
				"TopLevelClass$MemberClass".toCharArray());
		Assert.assertTrue("", constituent instanceof IMemberClass);
	}
}
