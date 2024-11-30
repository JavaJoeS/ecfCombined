/****************************************************************************
 * Copyright (c) 2011 Composent, Inc. and others.
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
package org.eclipse.ecf.examples.provider.remoteservice.identity;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;

public class RSExampleNamespace extends Namespace {

	private static final long serialVersionUID = 1235788855435011811L;
	public static final String SCHEME = "example";
	public static final String NAME = "ecf.namespace.example.rs";

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
	public ID createInstance(Object[] parameters) throws IDCreateException {
		// XXX Note that this assumes that a unique string is provided for creating the ID
		// e.g. IDFactory.getDefault().createID("myid");
		if (parameters == null || parameters.length < 1)
			throw new IDCreateException("parameters not of correct size");
		if (!(parameters[0] instanceof String))
			throw new IDCreateException("parameter not of String type");
		return new RSExampleID(this, (String) parameters[0]);
	}

	@Override
	public Class<?>[][] getSupportedParameterTypes() {
		return new Class<?>[][] { { String.class } };
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
	public String getScheme() {
		return SCHEME;
	}

}
