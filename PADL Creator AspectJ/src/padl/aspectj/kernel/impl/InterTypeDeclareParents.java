/*
 * Created on Aug 29, 2004
 *
 *
 * (c) Copyright 2001-2004 Jean-Yves Guyomarc'h,
 * University of Montr�al - ESSI.
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
package padl.aspectj.kernel.impl;

import padl.aspectj.kernel.IInterTypeDeclareParents;
import padl.kernel.IFirstClassEntity;
import padl.kernel.impl.Element;

/**
 * @author Jean-Yves
 */
public class InterTypeDeclareParents extends Element implements
		IInterTypeDeclareParents {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7447403607229450032L;
	private IFirstClassEntity target;
	private IFirstClassEntity declaredParent;

	public InterTypeDeclareParents(final char[] anID) {
		super(anID);
		this.target = null;
		this.declaredParent = null;
	}

	public IFirstClassEntity getDeclaredParent() {
		return this.declaredParent;
	}

	public IFirstClassEntity getTargetEntity() {
		return this.target;
	}

	public void setDeclaredParent(final IFirstClassEntity anEntity) {
		this.declaredParent = anEntity;

	}

	public void setTargetEntity(final IFirstClassEntity anEntity) {
		this.target = anEntity;

	}

}
