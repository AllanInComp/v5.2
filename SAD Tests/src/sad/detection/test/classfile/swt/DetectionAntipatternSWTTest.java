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
package sad.detection.test.classfile.swt;

import java.io.PrintWriter;
import junit.framework.Assert;
import junit.framework.TestCase;
import padl.generator.helper.ModelGenerator;
import padl.kernel.IIdiomLevelModel;
import sad.designsmell.detection.IDesignSmellDetection;
import sad.designsmell.detection.repository.Blob.BlobDetection;
import sad.designsmell.detection.repository.FunctionalDecomposition.FunctionalDecompositionDetection;
import sad.designsmell.detection.repository.SpaghettiCode.SpaghettiCodeDetection;
import sad.designsmell.detection.repository.SwissArmyKnife.SwissArmyKnifeDetection;
import util.io.ProxyDisk;

/**
 * @author Naouel Moha
 * @since  2005/12/04
 */
public final class DetectionAntipatternSWTTest extends TestCase {
	private static IIdiomLevelModel IdiomLevelModel;
	//	private static ModelAnnotatorLOC Annotator;
	private static final String PATH = "../SAD Tests/data/orgEclipseSwt.jar";
	private static final String MAIN_PATH = "../SAD Tests/data/";
	private static final String SYSTEM_NAME = DetectionAntipatternSWTTest.PATH
		.substring(DetectionAntipatternSWTTest.MAIN_PATH.length());

	public DetectionAntipatternSWTTest(final String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		if (DetectionAntipatternSWTTest.IdiomLevelModel == null) {
			//	final ICodeLevelModel codeLevelModel =
			//		Factory.getInstance().createCodeLevelModel(
			//			DetectionAntipatternSWTTest.PATH);
			//	codeLevelModel.create(new CompleteClassFileCreator(
			//		ConstituentRepository.getInstance(ClassFileRepository
			//			.getInstance(ConstituentRepository.class)),
			//		new String[] { DetectionAntipatternSWTTest.PATH },
			//		true));
			//	DetectionAntipatternSWTTest.IdiomLevelModel =
			//		(IIdiomLevelModel) new AACRelationshipsAnalysis()
			//			.invoke(codeLevelModel);
			//	DetectionAntipatternSWTTest.Annotator = new ModelAnnotatorLOC();
			//	DetectionAntipatternSWTTest.Annotator.annotateFromJARs(
			//		new String[] { DetectionAntipatternSWTTest.PATH },
			//		DetectionAntipatternSWTTest.IdiomLevelModel);

			DetectionAntipatternSWTTest.IdiomLevelModel =
				ModelGenerator
					.generateModelFromClassFilesDirectories(new String[] { DetectionAntipatternSWTTest.PATH });
		}
	}
	/** 
	 *  Test of the detection of Blobs
	 */
	public void testDetectBlobs() {
		final IDesignSmellDetection ad = new BlobDetection();
		// TODO Necessary?
		//	ad.setMetricsFileRepository(ClassFileRepository
		//		.getInstance(MetricRepository.class));
		ad.detect(DetectionAntipatternSWTTest.IdiomLevelModel);
		((BlobDetection) ad).output(new PrintWriter(ProxyDisk
			.getInstance()
			.fileTempOutput(
				DetectionAntipatternSWTTest.SYSTEM_NAME + "_Blob.ini")));
		Assert.assertEquals(
			"detectBlobs: Incorrect Number of Blobs found",
			3,
			ad.getDesignSmells().size());
	}

	/** 
	 *  Test of the detection of Functional Decompositions
	 */
	public void testDetectFunctionalDecompositions() {
		final IDesignSmellDetection ad = new FunctionalDecompositionDetection();
		// TODO Necessary?
		//	ad.setMetricsFileRepository(ClassFileRepository
		//		.getInstance(MetricRepository.class));
		ad.detect(DetectionAntipatternSWTTest.IdiomLevelModel);
		((FunctionalDecompositionDetection) ad).output(new PrintWriter(
			ProxyDisk.getInstance().fileTempOutput(
				DetectionAntipatternSWTTest.SYSTEM_NAME
						+ "_FunctionalDecomposition.ini")));
		Assert
			.assertEquals(
				"detectFunctionalDecompositions: Incorrect Number of FunctionalDecompositions found",
				0,
				ad.getDesignSmells().size());
	}

	/** 
	 *  Test of the detection of Poltergeists
	 */
	//	public void testDetectPoltergeists() {
	//		final IAntiPatternDetection ad =
	//			new PoltergeistDetection();
	//		ad.setModel(DetectionAntipatternSWTTest.IdiomLevelModel);
	//
	//		((PoltergeistDetection) ad).writeFile(
	//			DetectionAntipatternSWTTest.ProgramName + "_Poltergeist.ini");
	//
	//		DetectionAntipatternSWTTest.assertEquals(
	//			"detectPoltergeists: Incorrect Number of Poltergeists found",
	//			64, ad.getSetOfAntiPatterns().size());
	//	}
	/** 
	 *  Test of the detection of SpaghettiCodes
	 */
	public void testDetectSpaghettiCodes() {
		final IDesignSmellDetection ad = new SpaghettiCodeDetection();
		// TODO Necessary?
		//	ad.setMetricsFileRepository(ClassFileRepository
		//		.getInstance(MetricRepository.class));
		ad.detect(DetectionAntipatternSWTTest.IdiomLevelModel);
		((SpaghettiCodeDetection) ad)
			.output(new PrintWriter(ProxyDisk.getInstance().fileTempOutput(
				DetectionAntipatternSWTTest.SYSTEM_NAME + "_SpaghettiCode.ini")));
		Assert
			.assertEquals(
				"detectNumberSpaghettiCode: Incorrect Number of spaghetti codes found",
				0,
				ad.getDesignSmells().size());
	}

	/** 
	 *  Test of the detection of SwissArmyKnives
	 */
	public void testDetectSwissArmyKnives() {
		final IDesignSmellDetection ad = new SwissArmyKnifeDetection();
		ad.detect(DetectionAntipatternSWTTest.IdiomLevelModel);
		((SwissArmyKnifeDetection) ad).output(new PrintWriter(
			ProxyDisk
				.getInstance()
				.fileTempOutput(
					DetectionAntipatternSWTTest.SYSTEM_NAME
							+ "_SwissArmyKnife.ini")));
		Assert
			.assertEquals(
				"detectSwissArmyKnives: Incorrect Number of Swiss army knives found",
				0,
				ad.getDesignSmells().size());
	}
}