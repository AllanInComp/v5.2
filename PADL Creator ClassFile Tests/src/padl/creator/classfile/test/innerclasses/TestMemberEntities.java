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
import padl.analysis.UnsupportedSourceModelException;
import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.classfile.CompleteClassFileCreator;
import padl.creator.classfile.test.ClassFilePrimitive;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.IMemberClass;
import padl.kernel.exception.CreationException;
import padl.util.Util;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2006/02/08
 */
public class TestMemberEntities extends ClassFilePrimitive {
	private static IFirstClassEntity[] FirstClassEntities = null;

	public TestMemberEntities(final String aName) {
		super(aName);
	}
	protected void setUp() throws CreationException,
			UnsupportedSourceModelException {

		if (TestMemberEntities.FirstClassEntities == null) {
			final ICodeLevelModel codeLevelModel =
				ClassFilePrimitive.getFactory().createCodeLevelModel("");
			codeLevelModel
				.create(new CompleteClassFileCreator(
					new String[] {
							"../PADL Creator ClassFile Tests/rsc/ArgoUML/org/argouml/application/events/ArgoEventPump.class",
							"../PADL Creator ClassFile Tests/rsc/ArgoUML/org/argouml/application/events/ArgoEventPump$Pair.class" }));

			final IIdiomLevelModel idiomLevelModel =
				(IIdiomLevelModel) new AACRelationshipsAnalysis()
					.invoke(codeLevelModel);

			TestMemberEntities.FirstClassEntities =
				Util.getArrayOfTopLevelEntities(idiomLevelModel);
		}
	}
	public void testMemberEntities() {
		Assert.assertTrue(
			"Member class should have methods!",
			((IMemberClass) TestMemberEntities.FirstClassEntities[17]
				.getConstituentFromName("Pair")).getNumberOfConstituents() > 0);
	}
}
