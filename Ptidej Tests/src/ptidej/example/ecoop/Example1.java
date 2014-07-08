/*
 * (c) Copyright 2001-2003 Yann-Ga�l Gu�h�neuc,
 * �cole des Mines de Nantes and Object Technology International, Inc.
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
package ptidej.example.ecoop;

/**
 * @author Yann-Ga�l Gu�h�neuc
 */
public class Example1 {
	public static void main(final String[] args) {
		final Example1 example1 = new Example1();
		example1.addA(new A());
		// ...
	}
	private final A[] listOfAs = new A[10];

	private int numberOfAs = 0;
	public void addA(final A a) {
		// Complete implementation not shown here for lack of space.
		this.listOfAs[this.numberOfAs++] = a;
	}
	public A getA(final int index) {
		return this.listOfAs[index];
	}
	public void removeA(final A a) {
		// Complete implementation not shown here for lack of space.
	}
}
