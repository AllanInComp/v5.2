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
package ptidej.solver.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import padl.analysis.UnsupportedSourceModelException;
import padl.analysis.repository.AACRelationshipsAnalysis;
import padl.creator.classfile.CompleteClassFileCreator;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.IMethod;
import padl.kernel.IParameter;
import padl.kernel.exception.CreationException;
import padl.kernel.impl.Factory;
import padl.util.ModelStatistics;
import util.io.NullWriter;
import util.io.ProxyConsole;
import util.io.ProxyDisk;

/**
 * @author Yann-Ga�l Gu�h�neuc
 * @since  2007/02/21
 * @author Aminata Saban�
 * 2011/11/05
 * 
 */
public class SingletonFinderNew {
	public static void main(final String[] args) {
		//take as argument the configuration file and the path of result files
		//read the configuration file (.csv with ; as separator) that contains the paths of jars or bin
		//split each line into path and name

		ProxyConsole.getInstance().setDebugOutput(new NullWriter());
		ProxyConsole.getInstance().setNormalOutput(new NullWriter());
		ProxyConsole.getInstance().setErrorOutput(new NullWriter());

		if (args.length < 2) {
			System.out.println("This sprogram need 2 arguments :");
			System.out
				.println("1 - a csv file that contains on each line the path of the binaries or the jar to analyse and the name corresponding");
			System.out.println("1 - Results destination path");
			return;
		}
		final String destinationPath = args[1];

		try {
			final BufferedReader bufferedReader =
				new BufferedReader(new FileReader(args[0]));

			String line = bufferedReader.readLine();

			while (line != null) {

				final String[] lineContent = line.split(";");
				final String path = lineContent[0];
				final String name = lineContent[1];

				final ModelStatistics modelStatistics = new ModelStatistics();
				final ICodeLevelModel codeLevelModel =
					Factory.getInstance().createCodeLevelModel("");
				codeLevelModel.addModelListener(modelStatistics);
				codeLevelModel.create(new CompleteClassFileCreator(
					new String[] { path },
					true));

				final IIdiomLevelModel idiomLevelModel =
					(IIdiomLevelModel) new AACRelationshipsAnalysis()
						.invoke(codeLevelModel);

				final Writer writer =
					ProxyDisk.getInstance().fileAbsoluteOutput(
						new StringBuffer()
							.append(destinationPath)
							.append(File.separatorChar)
							.append("ConstraintResults in ")
							.append(name)
							.append(" for Singleton.ini")
							.toString());

				final Iterator entities =
					idiomLevelModel.getIteratorOnTopLevelEntities();
				int numberOfOccurrences = 0;
				while (entities.hasNext()) {
					final IFirstClassEntity firstClassEntity =
						(IFirstClassEntity) entities.next();
					final char[] entityId = firstClassEntity.getID();
					final Iterator methods =
						firstClassEntity
							.getIteratorOnConstituents(IMethod.class);
					while (methods.hasNext()) {
						final IMethod method = (IMethod) methods.next();
						if (method.isStatic()
								&& Arrays.equals(
									method.getReturnType(),
									entityId)
								&& method
									.getNumberOfConstituents(IParameter.class) < 2) {

							writer.write(Integer.toString(numberOfOccurrences));
							writer.write(".100.singleton = ");
							writer.write(entityId);
							writer.write('\n');

							numberOfOccurrences++;
						}
					}
				}

				writer.close();

				System.out.println(path);
				System.out.println(modelStatistics);
				System.out.println();

				line = bufferedReader.readLine();
			}

			bufferedReader.close();
		}
		catch (final FileNotFoundException e) {

			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
		catch (final CreationException e) {
			e.printStackTrace();
		}
		catch (final UnsupportedSourceModelException e) {
			e.printStackTrace();
		}

	}
}
