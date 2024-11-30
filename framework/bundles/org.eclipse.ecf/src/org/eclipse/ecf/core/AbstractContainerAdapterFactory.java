/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/

package org.eclipse.ecf.core;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * Abstract container adapter factory. This is an abstract implementation of the
 * {@link IAdapterFactory} interface.  Subclasses may be created as appropriate.
 */
public abstract class AbstractContainerAdapterFactory implements IAdapterFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 *      java.lang.Class)
	 */
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject == null || adapterType == null)
			return null;
		if (IContainer.class.isInstance(adaptableObject))
			return adapterType.cast(getContainerAdapter((IContainer) adaptableObject, adapterType));
		return null;
	}

	/**
	 * Method called by implementation of {@link #getAdapter(Object, Class)} if the
	 * adaptableObject is an instance of {@link IContainer}. Subclasses should implement to
	 * return the proper container adapter object based upon the given adapterType.
	 * 
	 * @param container the IContainer adaptable object provided to the adapter.  Will not be <code>null</code>.
	 * @param adapterType the type (interface) of the adapter on the given container.  Will not be <code>null</code>
	 * @return Object the container adapter instance.  May be <code>null</code>.
	 */
	protected abstract Object getContainerAdapter(IContainer container, Class adapterType);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public abstract Class<?>[] getAdapterList();

}
