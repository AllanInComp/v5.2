/* (c) Copyright 2011 and following years, Aminata SABAN�,
 * �Cole Polytechnique de Montr�al.
 * 
 * @author: Aminata SABAN�
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
package padl.creator.javafile.eclipse.test.comparator;

import junit.framework.TestCase;
import padl.creator.javafile.eclipse.test.util.RelaxedModelComparator;
import padl.creator.javafile.eclipse.test.util.Utils;
import padl.kernel.ICodeLevelModel;

public class LightModelsWithModelComparatorTest extends TestCase {
	public LightModelsWithModelComparatorTest(final String aName) {
		super(aName);
	}

	/**
	 * Compare two models created source code or binary classes based on all the
	 * source The comparison is done by ModelComparator
	 * 
	 */

	/*public void testModelsFromAllSrceComparison() {

		final String javaFilesFolderPath = "../Java Parser/src/";
		final String classPathEntry = "";
		final String classFilesFolderPath = "../Java Parser/bin/";

		// Model from source code

		final ICodeLevelModel padlModelFromJavaFiles = Utils
				.createJavaFilesPadlModel("", javaFilesFolderPath,
						classPathEntry);

		// Model from .class
		final ICodeLevelModel padlModelFromClassFiles = Utils
				.createJavaClassesPadlModel("", classFilesFolderPath);

		padlModelFromJavaFiles.walk(new RelaxedModelComparator(
				padlModelFromClassFiles));

		padlModelFromClassFiles.walk(new RelaxedModelComparator(
				padlModelFromClassFiles));

		padlModelFromJavaFiles.walk(new RelaxedModelComparator(
				padlModelFromJavaFiles));

	}*/

	/**
	 * Compare two models created source code and binary classes based on one
	 * class The comparison is done by ModelComparator
	 */

	public void testModelsFromOneFileComparison() {

		final String javaFilesFolderPath = "../Java Parser/src/";
		final String classPathEntry = "";
		final String[] javaFilesList =
			new String[] { "../Java Parser/src/parser/input/SourceInputsHolder.java" };
		final String classFilesFolderPath =
			"../Java Parser/bin/parser/input/SourceInputsHolder.class";

		// Model from source code

		final ICodeLevelModel padlModelFromJavaFiles =
			Utils.createLightJavaFilesPadlModel(
				"",
				javaFilesFolderPath,
				classPathEntry,
				javaFilesList);

		// Model from .class
		final ICodeLevelModel padlModelFromClassFiles =
			Utils.createLightJavaClassesPadlModel("", classFilesFolderPath);

		padlModelFromJavaFiles.walk(new RelaxedModelComparator(
			padlModelFromClassFiles));

	}

	/**
	 * Compare two models created source code and binary classes based on one
	 * class The comparison is done by ModelComparator
	 */

	public void testModelsFromOneFileComparison2() {
		final String javaFilesFolderPath =
			"../PADL Creator JavaFile (Eclipse) Tests/rsc/PADL testdata/";
		final String classPathEntry = "";
		final String[] javaFilesList =
			new String[] { "../PADL Creator JavaFile (Eclipse) Tests/rsc/PADL testdata/padl/example/interfaceComparator/IConstituent.java" };
		final String classFilesFolderPath =
			"../PADL Creator JavaFile (Eclipse) Tests/rsc/PADL testdata/padl/example/interfaceComparator/IConstituent.class";

		// Model from source code

		final ICodeLevelModel padlModelFromJavaFiles =
			Utils.createLightJavaFilesPadlModel(
				"",
				javaFilesFolderPath,
				classPathEntry,
				javaFilesList);

		// Model from .class
		final ICodeLevelModel padlModelFromClassFiles =
			Utils.createLightJavaClassesPadlModel("", classFilesFolderPath);

		padlModelFromJavaFiles.walk(new RelaxedModelComparator(
			padlModelFromJavaFiles));
		padlModelFromClassFiles.walk(new RelaxedModelComparator(
			padlModelFromClassFiles));
		padlModelFromJavaFiles.walk(new RelaxedModelComparator(
			padlModelFromClassFiles));
	}

}
