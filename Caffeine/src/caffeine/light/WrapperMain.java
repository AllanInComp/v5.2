/*
 * (c) Copyright 2001 Yann-Ga�l Gu�h�neuc,
 * Ecole des Mines de Nantes and Object Technology Internationl, Inc.
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
package caffeine.light;

import java.lang.reflect.Method;

import util.io.ProxyConsole;

/**
 * @version 	0.1
 * @author		Yann-Ga�l Gu�h�neuc
 */
public final class WrapperMain {
	public static void main(String[] args) {
		try {
			Class toBeRun = Class.forName(args[0]);
			Method mainMethod =
				toBeRun.getMethod("main", new Class[] { String[].class });
			final long startTime = System.currentTimeMillis();
			mainMethod.invoke(null, new Object[] { new String[0] });
			final long endTime = System.currentTimeMillis();
			System.out.println();
			System.out.println(endTime - startTime);
		}
		catch (final Exception e) {
			e.printStackTrace(ProxyConsole.getInstance().errorOutput());
		}
	}
}