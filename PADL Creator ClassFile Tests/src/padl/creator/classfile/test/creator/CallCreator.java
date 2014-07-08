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
package padl.creator.classfile.test.creator;

import java.io.PrintWriter;
import padl.analysis.UnsupportedSourceModelException;
import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.classfile.CompleteClassFileCreator;
import padl.creator.classfile.test.ClassFilePrimitive;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.exception.CreationException;
import padl.util.ModelStatistics;
import padl.visitor.IWalker;
import util.io.NullWriter;
import util.io.ProxyConsole;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2007/02/21
 */
public class CallCreator {
	public static void main(final String[] args) {
		final String[] paths =
			new String[] { "../DPL - JHotDraw v5.1/bin/",
					"../DPL - JRefactory v2.6.24/bin/",
					"../DPL - JUnit v3.7/bin/",
					"../DPL - MapperXML v1.9.7/bin/",
					"../DPL - QuickUML 2001/bin/" };

		ProxyConsole.getInstance().setDebugOutput(
			new PrintWriter(new NullWriter()));
		ProxyConsole.getInstance().setNormalOutput(new NullWriter());
		ProxyConsole.getInstance().setErrorOutput(new NullWriter());

		final IWalker walker = new InheritanceImplementationCounter();

		for (int i = 0; i < paths.length; i++) {
			final String path = paths[i];

			try {
				final ModelStatistics modelStatistics = new ModelStatistics();
				final ICodeLevelModel codeLevelModel =
					ClassFilePrimitive.getFactory().createCodeLevelModel("");
				codeLevelModel.addModelListener(modelStatistics);
				codeLevelModel.create(new CompleteClassFileCreator(
					new String[] { path },
					true));

				final IIdiomLevelModel idiomLevelModel =
					(IIdiomLevelModel) new AACRelationshipsAnalysis()
						.invoke(codeLevelModel);

				walker.reset();
				idiomLevelModel.walk(walker);

				System.out.println(path);
				System.out.println(modelStatistics);
				System.out.print("Number of inheritances/implementations: ");
				System.out.println(walker.getResult());
				System.out.println();
			}
			catch (final CreationException e) {
				e.printStackTrace(ProxyConsole.getInstance().errorOutput());
			}
			catch (final UnsupportedSourceModelException e) {
				e.printStackTrace(ProxyConsole.getInstance().errorOutput());
			}
		}
	}
}
