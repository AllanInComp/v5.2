/*
 * (c) Copyright 2000-2002 Yann-Ga�l Gu�h�neuc,
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
package padl.creator.classfile.test.example;

import java.util.Iterator;
import junit.framework.Assert;
import padl.creator.classfile.CompleteClassFileCreator;
import padl.creator.classfile.test.ClassFilePrimitive;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IGhost;
import padl.kernel.IInterface;
import padl.kernel.exception.CreationException;
import padl.util.Util;

/**
 * @version	0.2
 * @author 	Yann-Ga�l Gu�h�neuc
 * @since   2003/12/08
 */
public class Interfaces extends ClassFilePrimitive {
	private static IFirstClassEntity[] FirstClassEntities = null;

	public Interfaces(final String name) {
		super(name);
	}
	protected void setUp() throws CreationException {
		if (Interfaces.FirstClassEntities == null) {
			final ICodeLevelModel codeLevelModel =
				ClassFilePrimitive.getFactory().createCodeLevelModel(
					"padl.example.interfaces");

			// TODO: Load all types from padl.example.interfaces
			codeLevelModel
				.create(new CompleteClassFileCreator(
					new String[] {
							"../PADL Creator ClassFile Tests/bin/padl/example/interfaces/Element.class",
							"../PADL Creator ClassFile Tests/bin/padl/example/interfaces/IAdaptable.class",
							"../PADL Creator ClassFile Tests/bin/padl/example/interfaces/IJavaElement.class",
							"../PADL Creator ClassFile Tests/bin/padl/example/interfaces/Simple.class" }));

			Interfaces.FirstClassEntities =
				Util.getArrayOfTopLevelEntities(codeLevelModel);
		}
	}
	public void testNumberOfEntities() {
		Assert.assertEquals(
			"Number of entities",
			5,
			Interfaces.FirstClassEntities.length);
	}
	//	public void testObject() {
	//		Interfaces.assertAssigable(
	//			"Object ghost entity type",
	//			IGhost.class,
	//			Interfaces.Entities[0].getClass());
	//		Assert.assertEquals(
	//			"Object ghost entity name",
	//			"java.lang.Object",
	//			Interfaces.Entities[0].getDisplayName());
	//	}
	public void testAbstractDocument() {
		ClassFilePrimitive.assertAssigable(
			"AbstractDocument ghost entity type",
			IGhost.class,
			Interfaces.FirstClassEntities[0].getClass());
		Assert.assertEquals(
			"AbstractDocument ghost entity name",
			"padl.example.interfaces.AbstractDocument",
			Interfaces.FirstClassEntities[0].getDisplayID());
	}
	public void testElement() {
		ClassFilePrimitive.assertAssigable(
			"Element interface entity type",
			IInterface.class,
			Interfaces.FirstClassEntities[1].getClass());
		Assert.assertEquals(
			"Element interface entity name",
			"padl.example.interfaces.Element",
			Interfaces.FirstClassEntities[1].getDisplayID());

		final Iterator inheritedEntities =
			Interfaces.FirstClassEntities[1].getIteratorOnInheritedEntities();
		Assert.assertEquals(
			"Element interface number of inherited entities",
			true,
			inheritedEntities.hasNext());
		final Object object = inheritedEntities.next();
		Assert.assertEquals(
			"Element interface number of inherited entities",
			false,
			inheritedEntities.hasNext());
		ClassFilePrimitive.assertAssigable(
			"Element interface type of the super-entity",
			IGhost.class,
			object.getClass());
		Assert.assertEquals(
			"Element interface name of the super-entity",
			"padl.example.interfaces.AbstractDocument",
			((IGhost) object).getDisplayID());
	}
	public void testIAdaptable() {
		ClassFilePrimitive.assertAssigable(
			"IAdaptable interface entity type",
			IInterface.class,
			Interfaces.FirstClassEntities[2].getClass());
		Assert.assertEquals(
			"IAdaptable interface entity name",
			"padl.example.interfaces.IAdaptable",
			Interfaces.FirstClassEntities[2].getDisplayID());

		Assert.assertEquals(
			"IAdaptable interface number of inherited entities",
			false,
			Interfaces.FirstClassEntities[2]
				.getIteratorOnInheritedEntities()
				.hasNext());
		//	Interfaces.assertAssigable(
		//		"IAdaptable interface type of the super-entity",
		//		IGhost.class,
		//		inheritedEntities.get(0).getClass());
		//	Assert.assertEquals(
		//		"IAdaptable interface name of the super-entity",
		//		"java.lang.Object",
		//		((IGhost) inheritedEntities.get(0)).getDisplayName());
	}
	public void testIJavaElement() {
		ClassFilePrimitive.assertAssigable(
			"IJavaElement interface entity type",
			IInterface.class,
			Interfaces.FirstClassEntities[3].getClass());
		Assert.assertEquals(
			"IJavaElement interface entity name",
			"padl.example.interfaces.IJavaElement",
			Interfaces.FirstClassEntities[3].getDisplayID());

		final Iterator iterator =
			Interfaces.FirstClassEntities[3].getIteratorOnInheritedEntities();
		Assert.assertEquals(
			"IJavaElement interface number of inherited entities",
			true,
			iterator.hasNext());
		final Object object = iterator.next();
		Assert.assertEquals(
			"IJavaElement interface number of inherited entities",
			false,
			iterator.hasNext());
		ClassFilePrimitive.assertAssigable(
			"IJavaElement interface type of the super-entity",
			IInterface.class,
			object.getClass());
		Assert.assertEquals(
			"IJavaElement interface name of the super-entity",
			"padl.example.interfaces.IAdaptable",
			((IInterface) object).getDisplayID());
	}
	public void testSimple() {
		ClassFilePrimitive.assertAssigable(
			"Simple interface entity type",
			IInterface.class,
			Interfaces.FirstClassEntities[4].getClass());
		Assert.assertEquals(
			"Simple interface entity name",
			"padl.example.interfaces.Simple",
			Interfaces.FirstClassEntities[4].getDisplayID());

		Assert.assertEquals(
			"Simple interface number of inherited entities",
			false,
			Interfaces.FirstClassEntities[4]
				.getIteratorOnInheritedEntities()
				.hasNext());
		//	Interfaces.assertAssigable(
		//		"Simple interface type of the super-entity",
		//		IGhost.class,
		//		inheritedEntities.get(0).getClass());
		//	Assert.assertEquals(
		//		"Simple interface name of the super-entity",
		//		"java.lang.Object",
		//		((IGhost) inheritedEntities.get(0)).getDisplayName());
	}
}
