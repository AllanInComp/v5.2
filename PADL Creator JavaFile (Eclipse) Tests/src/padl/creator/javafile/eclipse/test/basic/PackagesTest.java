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
package padl.creator.javafile.eclipse.test.basic;

import junit.framework.Assert;
import junit.framework.TestCase;
import padl.creator.javafile.eclipse.test.util.Utils;
import padl.creator.javafile.eclipse.util.PadlParserUtil;
import padl.kernel.Constants;
import padl.kernel.IClass;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IPackage;
import padl.kernel.impl.Factory;

public class PackagesTest extends TestCase {

	//test package include in another package; no possibility to find it directly, it is not seen as a model constituent
	//but how find it in this case if we have only its id which is the same with its name and does not give information about
	//its container? 

	public PackagesTest(final String aName) {
		super(aName);

	}

	//	public void testPackageIncludedInAnotherPackage() {
	//		final String sourcePath =
	//			"../PADL Creator JavaFile (Eclipse) Tests/rsc/PADL testdata/";
	//		final String[] javaFiles =
	//			new String[] { "../PADL Creator JavaFile (Eclipse) Tests/rsc/PADL testdata/padl/example/packaje/" };
	//		final String classPathEntry = "";
	//
	//		ICodeLevelModel model = Factory.getInstance().createCodeLevelModel("");
	//		model =
	//			Utils.createJavaFilesPadlModel(
	//				"",
	//				sourcePath,
	//				classPathEntry,
	//				javaFiles);
	//
	//		IPackage packaje =
	//			(IPackage) PadlParserUtil.getPackage("padl.example.packaje", model);
	//		IPackage packaje1 = (IPackage) PadlParserUtil.getPackage("", model);
	//		Assert.assertNotNull(packaje);
	//		Assert.assertNull(packaje1);
	//
	//	}

	//test package and default package
	//test class in default package
	public void testDefaultPackages() {

		final String sourcePath =
			"../PADL Creator JavaFile (Eclipse) Tests/rsc/Aminata testdata/";
		final String[] javaFiles =
			new String[] {
					"../PADL Creator JavaFile (Eclipse) Tests/rsc/Aminata testdata/MyDefaultClass.java",
					"../PADL Creator JavaFile (Eclipse) Tests/rsc/Aminata testdata/MyDefaultClass2.java" };
		final String classPathEntry = "";

		final ICodeLevelModel model =
			Utils.createLightJavaFilesPadlModel(
				"",
				sourcePath,
				classPathEntry,
				javaFiles);

		final IPackage packag =
			(IPackage) model.getConstituentFromID(Constants.DEFAULT_PACKAGE_ID);

		Assert.assertNotNull(packag);

		final IClass clazz1 =
			(IClass) packag.getConstituentFromName("MyDefaultClass");
		Assert.assertNotNull(clazz1);
		final IClass clazz2 =
			(IClass) packag.getConstituentFromName("MyDefaultClass2");
		Assert.assertNotNull(clazz2);

		final IClass clazz3 =
			(IClass) packag.getConstituentFromName("MyDefaultClass1");
		Assert.assertNull(clazz3);

		final int nomberOfConstituentsExpected = 2;
		final int nomberOfConstituentsActual = packag.getNumberOfConstituents();

		Assert.assertEquals(
			nomberOfConstituentsExpected,
			nomberOfConstituentsActual);

	}

	public void testPackageIncludedInAnotherPackage1() {
		final String sourcePath =
			"../PADL Creator JavaFile (Eclipse) Tests/rsc/Aminata testdata/";
		final String[] javaFiles =
			new String[] {
					"../PADL Creator JavaFile (Eclipse) Tests/rsc/PADL testdata/padl/example/packaje/",
					"../PADL Creator JavaFile (Eclipse) Tests/rsc/Aminata testdata/MyDefaultClass.java" };
		final String classPathEntry = "";

		ICodeLevelModel model = Factory.getInstance().createCodeLevelModel("");
		model =
			Utils.createLightJavaFilesPadlModel(
				"",
				sourcePath,
				classPathEntry,
				javaFiles);

		final IPackage packaje1 = PadlParserUtil.getPackage("padl", model);
		final IPackage packaje2 =
			PadlParserUtil.getPackage("padl.example", model);
		final IPackage packaje3 =
			PadlParserUtil.getPackage("padl.example.packaje", model);
		final IPackage packaje4 =
			PadlParserUtil.getPackage("padl.example.packaje.toto", model);
		final IPackage packajeDefault = PadlParserUtil.getPackage("", model);

		Assert.assertNotNull(packaje1);
		Assert.assertNotNull(packaje2);
		Assert.assertNotNull(packaje3);
		Assert.assertNull(packaje4);
		Assert.assertNotNull(packajeDefault);
		Assert.assertEquals(
			packajeDefault.getID(),
			Constants.DEFAULT_PACKAGE_ID);
	}

}
