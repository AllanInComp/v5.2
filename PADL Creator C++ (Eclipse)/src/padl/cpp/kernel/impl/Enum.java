/*
 * (c) Copyright 2004 S�bastien Robidoux, Ward Flores,
 * Universit� de Montr�al
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 * Created 2004/08/18
 */
package padl.cpp.kernel.impl;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import padl.cpp.kernel.IEnum;
import padl.kernel.IElement;
import padl.kernel.IEntityMarker;
import padl.kernel.IFirstClassEntity;
import padl.kernel.impl.FirstClassEntity;

class Enum extends FirstClassEntity implements IEntityMarker, IEnum {
	private static final long serialVersionUID = 1124498017343286793L;

	private boolean forceAbstract = false;
	@SuppressWarnings("rawtypes")
	private final List<?> listOfSuperInterfaces = new ArrayList();

	public Enum(final char[] anID) {
		super(anID);
	}
	public boolean isForceAbstract() {
		return this.forceAbstract;
	}
	public List<?> listOfImplementedEntities() {
		return this.listOfSuperInterfaces;
	}
	public void setAbstract(final boolean aBoolean) {
		this.forceAbstract = aBoolean;
		super.setAbstract(aBoolean);
	}
	public void setVisibility(final int visibility) {
		super.setVisibility(this.isForceAbstract() ? visibility
				| Modifier.ABSTRACT : visibility);
	}
	public String toString() {
		final StringBuffer codeEq = new StringBuffer();
		codeEq.append(super.toString());
		codeEq.append(" Enum ");
		codeEq.append(this.getName());

		// Can enum have super-classes?
		// TODO Remove?
		final Iterator<?> iterator = this.listOfSuperInterfaces.iterator();
		if (iterator.hasNext()) {
			codeEq.append(" implements ");
			while (iterator.hasNext()) {
				codeEq.append(((IFirstClassEntity) iterator.next()).getName());
				if (iterator.hasNext()) {
					codeEq.append(", ");
				}
			}
		}
		return codeEq.toString();
	}
	public void attachTo(final IElement anElement) {
	}
	public void detach() {
	}
	public IElement getAttachedElement() {
		return null;
	}
}
