/* (c) Copyright 2011 and following years, Aminata SABAN�,
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
package padl.generator.helper.utils;

import padl.kernel.IAbstractModel;
import padl.kernel.IAbstractModelSerialiser;
import padl.serialiser.DB4OSerialiser;

public class PadlModelSerializer {
	public static String serializeModel(
		final IAbstractModel aModel,
		final String aTargetPath) {

		final IAbstractModelSerialiser serializer =
			DB4OSerialiser.getInstance();
		return serializer.serialiseWithAutomaticNaming(aModel, aTargetPath);
	}
	public static IAbstractModel deserializeModel(
		final String aSerialisedPADLModelFileName) {

		final IAbstractModelSerialiser serializer =
			DB4OSerialiser.getInstance();
		return serializer.deserialise(aSerialisedPADLModelFileName);
	}
}
