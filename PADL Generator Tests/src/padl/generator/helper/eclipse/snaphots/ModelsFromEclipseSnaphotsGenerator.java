/* (c) Copyright 2011 and following years, Aminata SABAN�,
 * �cole Polytechnique de Montr�al.
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
package padl.generator.helper.eclipse.snaphots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import padl.generator.helper.ModelGenerator;
import padl.generator.helper.ModelGeneratorCaller;
import padl.generator.helper.utils.FilesUtils;
import padl.generator.helper.utils.PadlModelSerializer;
import padl.generator.helper.utils.Untar;
import padl.kernel.ICodeLevelModel;
import padl.kernel.IIdiomLevelModel;
import util.io.ProxyConsole;
import util.io.ProxyDisk;

public class ModelsFromEclipseSnaphotsGenerator {

	public static void main(String[] args) {

		String snapshotsPath = "F:/Snapshots/Test/";

		//Lire la liste des zips � analyser
		List zipsList = FilesUtils.getZipsList(snapshotsPath);

		String serializationDirPath = "F:/Snapshots/Serialization/";
		String destPath = "F:/Snapshots/Dezip/";
		//	String runInfosPath= "F:/Snapshots/Run Infos/";

		System.out.println("Nombre de zips" + zipsList.size());

		//boucler sur cette liste
		for (int i = 0; i < zipsList.size(); i++) {

			System.out.println(i + " " + zipsList.get(i));

			String tarName = (String) zipsList.get(i);
			tarName = tarName.substring(tarName.lastIndexOf("\\") + 1);
			tarName = tarName.substring(0, tarName.length() - 7);
			System.out.println(" tarName " + tarName);

			String untaredPath = getString(new String[] { destPath, tarName });
			System.out.println(" untaredPath " + untaredPath);

			//detarrer
			Untar untar =
				new Untar((String) zipsList.get(i), new File(destPath));
			try {
				untar.untar();
			}
			catch (IOException e) {

				e.printStackTrace();
			}
			//processer pour ne garder que les fichiers java et .jar � la rigueur
			//ne doit on pas cibler certains repertoires et �viter les repertoires exemples pour les tests

			final Writer errorWriter =
				new BufferedWriter(ProxyDisk.getInstance().fileAbsoluteOutput(
					"rsc/errorOutput/" + tarName + "_errorOutput.txt"));
			final Writer normalWriter =
				new BufferedWriter(ProxyDisk.getInstance().fileAbsoluteOutput(
					"rsc/normalOutput/" + tarName + "_normalOutput.txt"));
			ProxyConsole.getInstance().setErrorOutput(errorWriter);
			ProxyConsole.getInstance().setNormalOutput(normalWriter);

			//cr�er le mod�le avec le generateur de Yann
			IIdiomLevelModel idiomLevelModel =
				ModelGenerator
					.generateModelFromJavaFilesDirectoryUsingEclipse(untaredPath);

			String outputPath =
				getString(new String[] { "rsc/output/", tarName,
						" (From Java Files, No Ghosts).classes)" });
			ModelGeneratorCaller.output(idiomLevelModel, outputPath);

			//serialiser le mod�le

			String modelSerializedPath =
				getString(new String[] { serializationDirPath, tarName });

			String deserializedPath =
				PadlModelSerializer.serializeModel(
					idiomLevelModel,
					modelSerializedPath);
			System.out.println("serialization " + deserializedPath);
			//deserializer le mod�le et le comparer au mod�le en cours
			ICodeLevelModel idiomLevelModelDeserialized =
				(ICodeLevelModel) PadlModelSerializer
					.deserializeModel(deserializedPath);

			System.out.println("before serialization "
					+ idiomLevelModel.getNumberOfTopLevelEntities()
					+ " constituents"
					+ idiomLevelModel.getNumberOfConstituents());
			System.out.println("before serialization "
					+ idiomLevelModelDeserialized.getNumberOfTopLevelEntities()
					+ " constituents"
					+ idiomLevelModelDeserialized.getNumberOfConstituents());
			//supprimer le repertoire dezipp�

			//FilesUtils.deleteDir(new File(untaredPath));
		}

		//voir avec Yann s'il faut mettre cela dans une autre classe pour ne pas cr�er trop de liens avec d'autres projets

		//ne vaut il pas mieux faire une autre classe pour modelGeneratorfromjavafiles
	}
	private static String getString(String[] tokens) {
		StringBuffer tmpString = new StringBuffer();
		for (int i = 0; i < tokens.length; i++) {
			tmpString.append(tokens[i]);
		}

		return tmpString.toString();

	}
}
