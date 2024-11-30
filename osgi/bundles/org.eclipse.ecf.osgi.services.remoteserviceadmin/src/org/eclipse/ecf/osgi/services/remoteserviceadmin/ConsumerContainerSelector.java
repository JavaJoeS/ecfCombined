/****************************************************************************
 * Copyright (c) 2010-2011 Composent, Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import java.util.List;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.PropertiesUtil;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;

/**
 * Default implementation of {@link IConsumerContainerSelector}.
 * 
 */
public class ConsumerContainerSelector extends
		AbstractConsumerContainerSelector implements IConsumerContainerSelector {

	private static final boolean reuseExistingContainers = new Boolean(
			System.getProperty(
					"org.eclipse.ecf.osgi.services.remoteserviceadmin.ConsumerContainerSelector.reuseExistingContainers", //$NON-NLS-1$
					"true")).booleanValue(); //$NON-NLS-1$

	private boolean autoCreateContainer = false;

	public ConsumerContainerSelector(boolean autoCreateContainer) {
		this.autoCreateContainer = autoCreateContainer;
	}

	public IRemoteServiceContainer selectConsumerContainer(
			EndpointDescription endpointDescription)
			throws SelectContainerException {
		trace("selectConsumerContainer", "endpointDescription=" + endpointDescription); //$NON-NLS-1$ //$NON-NLS-2$

		// Get service.imported.configs
		List<String> sic = PropertiesUtil
				.getStringPlusProperty(
						endpointDescription.getProperties(),
						org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS);
		String[] serviceImportedConfigs = sic.toArray(new String[sic.size()]);
		// Get the endpointID
		ID endpointContainerID = endpointDescription.getContainerID();

		// Get connect targetID
		ID connectTargetID = endpointDescription.getConnectTargetID();

		IRemoteServiceContainer rsContainer = (reuseExistingContainers) ? selectExistingConsumerContainer(
				endpointContainerID, serviceImportedConfigs, connectTargetID)
				: null;

		// If we haven't found any existing containers then we create one
		// from the remoteSupportedConfigs...*iff* autoCreateContainer is
		// set to true
		if (rsContainer == null && autoCreateContainer)
			rsContainer = createAndConfigureConsumerContainer(
					serviceImportedConfigs, endpointDescription.getProperties());

		// Get the connect target ID from the endpointDescription
		// and connect the given containers to the connect targetID
		// This is only needed when when the endpointID is different from
		// the connect targetID, and the containers are not already
		// connected
		connectContainerToTarget(rsContainer, connectTargetID);

		trace("selectConsumerContainer", "rsContainer selected=" + rsContainer); //$NON-NLS-1$ //$NON-NLS-2$

		return rsContainer;
	}

	public void close() {
	}

}
