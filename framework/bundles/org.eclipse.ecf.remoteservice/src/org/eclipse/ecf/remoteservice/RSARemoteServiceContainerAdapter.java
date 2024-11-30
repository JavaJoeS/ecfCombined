/****************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others.
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
package org.eclipse.ecf.remoteservice;

import java.util.Dictionary;
import java.util.Map;
import org.eclipse.equinox.concurrent.future.IExecutor;

/**
 * A container adapter intended for use by remote service host containers.  Implements IRemoteServiceContainerAdapter
 * and IRSAHostContainerAdapter.  A IRSAHostContainerAdapter that gets the actual registerService call
 * is expected to be provided upon construction.  
 * 
 * Subclasses should extend as appropriate.
 * 
 * @since 8.9
 */
public class RSARemoteServiceContainerAdapter extends RemoteServiceContainerAdapterImpl {

	public RSARemoteServiceContainerAdapter(AbstractRSAContainer container, IExecutor executor) {
		super(container, executor);
	}

	public RSARemoteServiceContainerAdapter(AbstractRSAContainer container) {
		super(container);
	}

	protected AbstractRSAContainer getRSAContainer() {
		return (AbstractRSAContainer) super.getContainer();
	}

	@Override
	protected RemoteServiceRegistrationImpl createRegistration() {
		return new RSARemoteServiceRegistration();
	}

	public class RSARemoteServiceRegistration extends RemoteServiceRegistrationImpl implements IExtendedRemoteServiceRegistration {
		private static final long serialVersionUID = 3245045338579222364L;

		private Map<String, Object> extraProperties;

		@Override
		public void publish(RemoteServiceRegistryImpl reg, Object svc, String[] clzzes, Dictionary props) {
			super.publish(reg, svc, clzzes, props);
			try {
				this.extraProperties = getRSAContainer().exportRemoteService(this);
			} catch (RuntimeException t) {
				// unpublish in the event of 
				reg.unpublishService(this);
				throw t;
			}
		}

		public void unregister() {
			RemoteServiceRegistryImpl regis = getRegistry();
			if (regis != null) {
				regis.unpublishService(this);
				getRSAContainer().unexportRemoteService(this);
			}
		}

		public Map<String, Object> getExtraProperties() {
			return extraProperties;
		}
	}

}
