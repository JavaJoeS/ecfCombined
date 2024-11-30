/****************************************************************************
 * Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 *  Contributors:
 *    Wim Jongman - initial API and implementation 
 *    Ahmed Aadel - initial API and implementation     
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core.internal;

import java.util.concurrent.Executors;

import org.eclipse.ecf.discovery.IServiceListener;
import org.eclipse.ecf.discovery.IServiceTypeListener;
import org.eclipse.ecf.provider.zookeeper.DiscoveryActivator;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Localizer {

	// IServiceListener tracker
	private ServiceTracker serviceListenerTracker;
	// IServiceTypeListener tracker
	private ServiceTracker serviceTypeListenerTracker;

	private static Localizer singleton;

	private Localizer() {
		// Singleton
	}

	public static Localizer getSingleton() {
		if (singleton == null) {
			singleton = new Localizer();
		}
		return singleton;
	}

	public void init() {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			public void run() {
				BundleContext context = DiscoveryActivator.getContext();
				final ZooDiscoveryContainer discovery = ZooDiscoveryContainer
						.getSingleton();
				serviceListenerTracker = new ServiceTracker(context,
						IServiceListener.class.getName(), null) {
					public Object addingService(ServiceReference reference) {
						discovery.addServiceListener((IServiceListener) context
								.getService(reference));
						return super.addingService(reference);
					}

					public void modifiedService(ServiceReference reference,
							Object service) {
						discovery
								.removeServiceListener((IServiceListener) context
										.getService(reference));
						discovery.addServiceListener((IServiceListener) context
								.getService(reference));
						super.modifiedService(reference, service);
					}

					public void removedService(ServiceReference reference,
							Object service) {
						discovery
								.removeServiceListener((IServiceListener) context
										.getService(reference));
						super.removedService(reference, service);
					}
				};
				serviceListenerTracker.open(true);
				serviceTypeListenerTracker = new ServiceTracker(context,
						IServiceTypeListener.class.getName(), null) {
					public Object addingService(ServiceReference reference) {
						discovery
								.addServiceTypeListener((IServiceTypeListener) context
										.getService(reference));
						return super.addingService(reference);
					}

					public void modifiedService(ServiceReference reference,
							Object service) {
						discovery
								.removeServiceTypeListener((IServiceTypeListener) context
										.getService(reference));
						discovery
								.addServiceTypeListener((IServiceTypeListener) context
										.getService(reference));
						super.modifiedService(reference, service);
					}

					public void removedService(ServiceReference reference,
							Object service) {
						discovery
								.removeServiceTypeListener((IServiceTypeListener) context
										.getService(reference));
						super.removedService(reference, service);
					}
				};
				serviceTypeListenerTracker.open(true);
			}
		});
	}

	public void close() {
		if (this.serviceListenerTracker != null) {
			this.serviceListenerTracker.close();
		}
		if (this.serviceTypeListenerTracker != null) {
			this.serviceTypeListenerTracker.close();
		}
	}

	public void localize(Notification notification) {
		if (notification.getType() == Notification.AVAILABLE) {
			// inform listeners interested in this service type
			for (IServiceListener srl : ZooDiscoveryContainer
					.getSingleton()
					.getServiceListenersForType(notification.getServiceTypeID())) {
				srl.serviceDiscovered(notification);
			}
			// inform service type listeners
			for (IServiceTypeListener stl : ZooDiscoveryContainer
					.getSingleton().getServiceTypeListeners()) {
				stl.serviceTypeDiscovered(notification);
			}
		} else if (notification.getType() == Notification.UNAVAILABLE) {
			// inform all service listeners about this lost service
			for (IServiceListener l : ZooDiscoveryContainer.getSingleton()
					.getAllServiceListeners()) {
				l.serviceUndiscovered(notification);
			}
		}
	}
}
