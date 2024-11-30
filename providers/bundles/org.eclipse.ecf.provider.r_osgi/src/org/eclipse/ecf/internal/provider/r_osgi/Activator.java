/****************************************************************************
 * Copyright (c) 2008 Jan S. Rellermeyer, and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Jan S. Rellermeyer - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/

package org.eclipse.ecf.internal.provider.r_osgi;

import ch.ethz.iks.r_osgi.RemoteOSGiService;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.BundleStarter;
import org.eclipse.ecf.core.util.ExtensionRegistryRunnable;
import org.eclipse.ecf.provider.r_osgi.identity.*;
import org.eclipse.equinox.concurrent.future.IExecutor;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 */
public final class Activator implements BundleActivator {

	// This is a service property that is used to find any IExecutor service instances
	// to be used for synchronous or asynchronous execution.  The use of the IExecutor
	// The type of the value for this property is String, and valid values are 'sync' or 'async'
	// For example, to setup one's own executor implementation to use for async invocation
	// on a consumer would look like this
	// 
	// props.put("org.eclipse.ecf.provider.r_osgi.consumerExecutor","async");
	// bundleContext.registerService(IExecutor.class.getName(), new MyExecutor(), props);
	// 
	public static final String CONSUMER_SYNC_EXECUTOR_TYPE = "org.eclipse.ecf.provider.r_osgi.consumerExecutor"; //$NON-NLS-1$

	// see bug 495535
	private static final boolean DELETE_PROXY_BUNDLES_ON_INIT = new Boolean(System.getProperty("ch.ethz.iks.r_osgi.deleteProxyBundlesOnInit", "true")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ecf.provider.r_osgi"; //$NON-NLS-1$

	// The shared instance
	static Activator plugin;

	// The bundle context
	private BundleContext context;

	// The service tracker for the R-OSGi remote service
	private ServiceTracker r_osgi_tracker;

	/**
	 * Called when the OSGi framework starts the bundle.
	 * 
	 * @param bc
	 *            the bundle context.
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext bc) throws Exception {
		plugin = this;
		this.context = bc;
		// start ch.ethz.iks.r_osgi.remote bundle
		BundleStarter.startDependents(context, new String[] {"ch.ethz.iks.r_osgi.remote", "org.eclipse.ecf.provider"}, Bundle.RESOLVED | Bundle.STARTING); //$NON-NLS-1$ //$NON-NLS-2$

		r_osgi_tracker = new ServiceTracker(context, RemoteOSGiService.class.getName(), null);
		r_osgi_tracker.open();
		// bug 495535
		if (DELETE_PROXY_BUNDLES_ON_INIT)
			deleteProxyBundles();

		SafeRunner.run(new ExtensionRegistryRunnable(bc) {
			protected void runWithoutRegistry() throws Exception {
				bc.registerService(Namespace.class, new R_OSGiNamespace(), null);
				bc.registerService(Namespace.class, new R_OSGiWSNamespace(), null);
				bc.registerService(Namespace.class, new R_OSGiWSSNamespace(), null);
				bc.registerService(Namespace.class, new R_OSGiRemoteServiceNamespace(), null);
				bc.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(R_OSGiContainerInstantiator.NAME, new R_OSGiContainerInstantiator(), "R_OSGi Container", true, false), null); //$NON-NLS-1$
				bc.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(R_OSGiContainerInstantiator.NAME_HTTP, new R_OSGiContainerInstantiator(), "R_OSGi Websockets Container", true, false), null); //$NON-NLS-1$
				bc.registerService(ContainerTypeDescription.class, new ContainerTypeDescription(R_OSGiContainerInstantiator.NAME_HTTPS, new R_OSGiContainerInstantiator(), "R_OSGi Secure Websockets Container", true, false), null); //$NON-NLS-1$
			}
		});
	}

	private void deleteProxyBundles() {
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle b = bundles[i];
			String bName = b.getSymbolicName();
			if (bName.startsWith(RemoteOSGiService.R_OSGi_PROXY_PREFIX))
				try {
					b.uninstall();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Called when the OSGi framework stops the bundle.
	 * 
	 * @param bc
	 *            the bundle context.
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext bc) throws Exception {
		r_osgi_tracker.close();
		r_osgi_tracker = null;
		synchronized (executorServiceTrackerLock) {
			if (executorServiceTracker != null) {
				executorServiceTracker.close();
				executorServiceTracker = null;
			}
		}
		this.context = null;
		plugin = null;
	}

	/**
	 * get the bundle context.
	 * 
	 * @return the bundle context.
	 */
	public BundleContext getContext() {
		return context;
	}

	/**
	 * get the R-OSGi service instance.
	 * 
	 * @return the R-OSGi service instance or null, if there is none.
	 */
	public RemoteOSGiService getRemoteOSGiService() {
		if (r_osgi_tracker == null) {
			return null;
		}
		return (RemoteOSGiService) r_osgi_tracker.getService();
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	private ServiceTracker executorServiceTracker;
	private final Object executorServiceTrackerLock = new Object();

	private Filter createExecutorFilter(boolean sync) {
		try {
			return getContext().createFilter("(&(" + Constants.OBJECTCLASS + "=" + IExecutor.class.getName() + ")(" + CONSUMER_SYNC_EXECUTOR_TYPE + "=" + ((sync) ? "sync" : "async") + "))"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		} catch (InvalidSyntaxException e) {
			// should not happen
			return null;
		}
	}

	public IExecutor getExecutor(boolean sync) {
		synchronized (executorServiceTrackerLock) {
			if (executorServiceTracker == null) {
				Filter syncExecutorFilter = createExecutorFilter(sync);
				executorServiceTracker = new ServiceTracker(getContext(), syncExecutorFilter, null);
				executorServiceTracker.open();
			}
		}
		return (IExecutor) executorServiceTracker.getService();
	}
}
