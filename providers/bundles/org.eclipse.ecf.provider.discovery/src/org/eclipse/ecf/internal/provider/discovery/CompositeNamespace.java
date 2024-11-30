/****************************************************************************
 * Copyright (c) 2008 Versant Corp.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.internal.provider.discovery;

import java.net.URI;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceTypeID;

public class CompositeNamespace extends Namespace {

	private static final long serialVersionUID = -4774766051014928510L;
	public static final String NAME = "ecf.namespace.composite"; //$NON-NLS-1$

	public CompositeNamespace() {
		super(NAME, "Composite Namespace"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
	public ID createInstance(final Object[] parameters) {
		if (parameters == null || parameters.length < 1 || parameters.length > 2) {
			throw new IDCreateException("parameter count must be non null and of length >= 1 and =< 2"); //$NON-NLS-1$
		} else if (parameters.length == 2 && parameters[0] instanceof String && parameters[1] instanceof URI) {
			return new CompositeServiceID(this, new ServiceTypeID(this, (String) parameters[0]), (URI) parameters[1]);
		} else if (parameters.length == 2 && parameters[0] instanceof IServiceTypeID && parameters[1] instanceof URI) {
			return new CompositeServiceID(this, (IServiceTypeID) parameters[0], (URI) parameters[1]);
		} else if (parameters.length == 1 && parameters[0] instanceof IServiceTypeID) {
			final IServiceTypeID iServiceTypeID = (IServiceTypeID) parameters[0];
			return new ServiceTypeID(this, iServiceTypeID.getName());
		} else {
			throw new IDCreateException("wrong parameters"); //$NON-NLS-1$
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
	public String getScheme() {
		return "composite"; //$NON-NLS-1$
	}

	@Override
	public Class<?>[][] getSupportedParameterTypes() {
		return new Class<?>[][] {{String.class, URI.class}, {IServiceTypeID.class, URI.class}, {IServiceTypeID.class}};
	}
}
