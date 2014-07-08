package padl.creator.classfile.test.topLevelEntity;

import junit.framework.Assert;
import junit.framework.TestCase;
import padl.creator.classfile.CompleteClassFileCreator;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.exception.CreationException;
import padl.kernel.impl.Factory;

public class TopLevelEntityTest extends TestCase {
	public TopLevelEntityTest(String name) {
		super(name);
	}

	/**
	 * Weird - we find in the list of top level entities some members classes
	 * but this seems not to occur all the time. Example, with only the package
	 * swing 1.3.1, the phemenon does not occur (public void
	 * testClassMemberIsTopLevelEntity1()) but with all rt 1.3.1 (public void
	 * testClassMe
	 * 
	 * mberIsTopLevelEntity2()), it happens we fix it by explicitely
	 * forbid the insertion of member classes in the list of top level entities
	 */
	public void testClassMemberIsTopLevelEntity1() {
		final ICodeLevelModel model =
			Factory.getInstance().createCodeLevelModel("");

		try {
			model
				.create(new CompleteClassFileCreator(
					new String[] { "../PADL Creator ClassFile Tests/rsc/TopLevelEntities/swing 1.3.1/" },
					true));
		}
		catch (final CreationException e) {
			e.printStackTrace();
		}

		final IFirstClassEntity entity =
			model.getTopLevelEntityFromID("javax.swing.JTable");
		Assert.assertNotNull(entity);

		final IFirstClassEntity memberEntity =
			model.getTopLevelEntityFromID("javax.swing.JTable$DateRenderer");
		Assert.assertNull(memberEntity);
	}

	public void testClassMemberIsTopLevelEntity2() {
		final ICodeLevelModel model =
			Factory.getInstance().createCodeLevelModel("");

		try {
			model
				.create(new CompleteClassFileCreator(
					new String[] { "../PADL Creator ClassFile Tests/rsc/TopLevelEntities/rt 1.3.1/" },
					true));
		}
		catch (final CreationException e) {
			e.printStackTrace();
		}

		final IFirstClassEntity entity =
			model.getTopLevelEntityFromID("javax.swing.JTable");
		Assert.assertNotNull(entity);

		final IFirstClassEntity memberEntity =
			model.getTopLevelEntityFromID("javax.swing.JTable$DateRenderer");
		Assert.assertNull(memberEntity);
	}

	public void testClassMemberInAnonymousIsTopLevelEntity() {
		final ICodeLevelModel model =
			Factory.getInstance().createCodeLevelModel("");

		try {
			model
				.create(new CompleteClassFileCreator(
					new String[] { "../PADL Creator ClassFile Tests/rsc/TopLevelEntities/swing 1.3.1/" },
					true));
		}
		catch (final CreationException e) {
			e.printStackTrace();
		}

		final IFirstClassEntity entity =
			model
				.getTopLevelEntityFromID("javax.swing.JSlider$1$SmartHashtable");
		Assert.assertNull(entity);
	}
}
