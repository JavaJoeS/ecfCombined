package org.eclipse.ecf.remoteservice;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.remoteservice.Activator;
import org.eclipse.ecf.remoteservice.events.*;
import org.eclipse.ecf.remoteservice.util.RemoteFilterImpl;
import org.eclipse.equinox.concurrent.future.*;
import org.osgi.framework.InvalidSyntaxException;

/**
 * @since 8.3
 */
public class RemoteServiceContainerAdapterImpl implements IRemoteServiceContainerAdapter {

	private IExecutor executor;
	private IContainer container;
	private IRemoteServiceCallPolicy remoteServiceCallPolicy;
	private IConnectContext connectContext;

	protected RemoteServiceRegistryImpl registry;

	protected final List<IRemoteServiceListener> listeners;

	protected final Map<IRemoteServiceReference, List<AbstractRemoteService>> refToImplMap;

	public RemoteServiceContainerAdapterImpl(IContainer container, IExecutor executor) {
		Assert.isNotNull(container);
		this.container = container;
		listeners = new ArrayList<IRemoteServiceListener>();
		refToImplMap = new HashMap<IRemoteServiceReference, List<AbstractRemoteService>>();
		setRegistry(new RemoteServiceRegistryImpl(container.getID()));
		setExecutor(executor);
	}

	public RemoteServiceContainerAdapterImpl(IContainer container) {
		this(container, null);
	}

	public void dispose() {
		if (registry != null) {
			RemoteServiceRegistrationImpl[] registrations = registry.getRegistrations();
			if (registrations != null)
				for (RemoteServiceRegistrationImpl reg : registrations)
					fireRemoteServiceListeners(createUnregisteredEvent(reg));

			// Now if there are no listeners we will unregister them ourselves
			registrations = registry.getRegistrations();
			if (registrations != null)
				for (RemoteServiceRegistrationImpl reg : registrations)
					reg.unregister();

			registry.unpublishServices();
			registry = null;
		}
		synchronized (listeners) {
			listeners.clear();
		}
		synchronized (refToImplMap) {
			refToImplMap.clear();
		}
		this.connectContext = null;
		this.remoteServiceCallPolicy = null;
		this.container = null;
		this.executor = null;
	}

	public void addRemoteServiceListener(IRemoteServiceListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeRemoteServiceListener(IRemoteServiceListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	public void setConnectContextForAuthentication(IConnectContext connectContext) {
		this.connectContext = connectContext;
	}

	public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
		this.remoteServiceCallPolicy = policy;
		return true;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public IRemoteServiceRegistration registerRemoteService(String[] clazzes, Object service, Dictionary properties) {
		Trace.entering(Activator.PLUGIN_ID, IRemoteServiceImplDebugOptions.METHODS_ENTERING, this.getClass(), "registerRemoteService", new Object[] {clazzes, service, properties}); //$NON-NLS-1$
		if (service == null)
			throw new NullPointerException("service cannot be null"); //$NON-NLS-1$
		if (clazzes.length == 0)
			throw new IllegalArgumentException("service classes cannot be empty"); //$NON-NLS-1$

		final String[] copy = new String[clazzes.length];
		for (int i = 0; i < clazzes.length; i++)
			copy[i] = new String(clazzes[i].getBytes());
		clazzes = copy;

		final String invalidService = checkServiceClass(clazzes, service);
		if (invalidService != null)
			throw new IllegalArgumentException("Service=" + invalidService + " is invalid"); //$NON-NLS-1$ //$NON-NLS-2$

		RemoteServiceRegistryImpl reg = getRegistry();
		if (reg == null)
			throw new NullPointerException("registry cannot be null"); //$NON-NLS-1$

		RemoteServiceRegistrationImpl registration = createRegistration();
		synchronized (registry) {
			registration.publish(reg, service, clazzes, properties);
		}
		fireRemoteServiceListeners(createRegisteredEvent(registration));
		Trace.exiting(Activator.PLUGIN_ID, IRemoteServiceImplDebugOptions.METHODS_EXITING, this.getClass(), "registerRemoteService", registration); //$NON-NLS-1$
		return registration;
	}

	public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
		Trace.entering(Activator.PLUGIN_ID, IRemoteServiceImplDebugOptions.METHODS_ENTERING, this.getClass(), "getRemoteServiceReferences", new Object[] {target, idFilter, clazz, filter}); //$NON-NLS-1$
		RemoteServiceRegistryImpl reg = getRegistry();
		if (reg == null)
			return null;
		if (target != null)
			connectToRemoteServiceTarget(target);

		final IRemoteFilter remoteFilter = (filter == null) ? null : new RemoteFilterImpl(filter);
		// then from the local registry
		ID localContainerID = getLocalContainerID();
		// Now we lookup remote service references
		final List<IRemoteServiceReference> references = new ArrayList();
		if (idFilter == null || Arrays.asList(idFilter).contains(localContainerID)) {
			synchronized (reg) {
				final IRemoteServiceReference[] rs = registry.lookupServiceReferences(clazz, remoteFilter);
				if (rs != null)
					for (int j = 0; j < rs.length; j++)
						references.add(rs[j]);
			}
		}
		// And we return the result
		final IRemoteServiceReference[] result = references.toArray(new IRemoteServiceReference[references.size()]);
		Trace.exiting(Activator.PLUGIN_ID, IRemoteServiceImplDebugOptions.METHODS_EXITING, this.getClass(), "getRemoteServiceReferences", result); //$NON-NLS-1$
		return (result.length == 0) ? null : result;
	}

	public IFuture asyncGetRemoteServiceReferences(final ID target, final ID[] idFilter, final String clazz, final String filter) {
		IExecutor exec = getExecutor();
		if (exec == null)
			throw new NullPointerException("Executor is null.  Cannot asynchronously get remote service references"); //$NON-NLS-1$
		return exec.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				return getRemoteServiceReferences(target, idFilter, clazz, filter);
			}
		}, null);
	}

	public IRemoteServiceReference[] getRemoteServiceReferences(ID[] idFilter, String clazz, String filter) throws InvalidSyntaxException {
		try {
			return getRemoteServiceReferences(null, idFilter, clazz, filter);
		} catch (ContainerConnectException e) {
			// can't occur, because first parameter is null
			return null;
		}
	}

	public IRemoteServiceReference[] getRemoteServiceReferences(ID target, String clazz, String filter) throws InvalidSyntaxException, ContainerConnectException {
		return getRemoteServiceReferences(target, null, clazz, filter);
	}

	public IFuture asyncGetRemoteServiceReferences(final ID[] idFilter, final String clazz, final String filter) {
		IExecutor exec = getExecutor();
		if (exec == null)
			throw new NullPointerException("Executor is null.  Cannot asynchronously get remote service references"); //$NON-NLS-1$
		return executor.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				return getRemoteServiceReferences(idFilter, clazz, filter);
			}
		}, null);
	}

	public IFuture asyncGetRemoteServiceReferences(final ID target, final String clazz, final String filter) {
		IExecutor exec = getExecutor();
		if (exec == null)
			throw new NullPointerException("Executor is null.  Cannot asynchronously get remote service references"); //$NON-NLS-1$
		return executor.execute(new IProgressRunnable() {
			public Object run(IProgressMonitor monitor) throws Exception {
				return getRemoteServiceReferences(target, clazz, filter);
			}
		}, null);
	}

	public IRemoteServiceReference[] getAllRemoteServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
		final IRemoteServiceReference[] result = getRemoteServiceReferences((ID[]) null, clazz, filter);
		if (result == null)
			return null;
		return (result.length == 0) ? null : result;
	}

	public Namespace getRemoteServiceNamespace() {
		return IDFactory.getDefault().getNamespaceByName(RemoteServiceNamespace.NAME);
	}

	public IRemoteServiceID getRemoteServiceID(ID containerID, long containerRelativeID) {
		if (containerID == null)
			return null;
		RemoteServiceRegistryImpl reg = getRegistry();
		if (reg == null)
			return null;
		ID localContainerID = getLocalContainerID();
		if (containerID.equals(localContainerID)) {
			synchronized (reg) {
				RemoteServiceRegistrationImpl registration = reg.findRegistrationForServiceId(containerRelativeID);
				if (registration != null)
					return registration.getID();
			}
		}
		return null;
	}

	public IRemoteServiceReference getRemoteServiceReference(IRemoteServiceID serviceID) {
		ID containerID = serviceID.getContainerID();
		if (containerID == null)
			return null;
		RemoteServiceRegistryImpl reg = getRegistry();
		if (reg == null)
			return null;
		RemoteServiceRegistrationImpl registration = null;
		ID localContainerID = getLocalContainerID();
		if (containerID.equals(localContainerID)) {
			synchronized (reg) {
				registration = reg.findRegistrationForServiceId(serviceID.getContainerRelativeID());
				if (registration != null)
					return registration.getReference();
			}
		}
		return (registration == null) ? null : registration.getReference();
	}

	public IRemoteService getRemoteService(IRemoteServiceReference reference) {
		Trace.entering(Activator.PLUGIN_ID, IRemoteServiceImplDebugOptions.METHODS_ENTERING, this.getClass(), "getRemoteService", reference); //$NON-NLS-1$
		final RemoteServiceRegistrationImpl registration = getRemoteServiceRegistrationImpl(reference);
		if (registration == null)
			return null;
		final AbstractRemoteService remoteService = createRemoteService(registration);
		if (remoteService == null)
			return null;
		synchronized (refToImplMap) {
			List<AbstractRemoteService> remoteServiceImplList = refToImplMap.get(reference);
			if (remoteServiceImplList == null)
				remoteServiceImplList = new ArrayList();
			remoteServiceImplList.add(remoteService);
			refToImplMap.put(reference, remoteServiceImplList);
		}
		Trace.exiting(Activator.PLUGIN_ID, IRemoteServiceImplDebugOptions.METHODS_EXITING, this.getClass(), "getRemoteService", remoteService); //$NON-NLS-1$
		return remoteService;
	}

	public boolean ungetRemoteService(IRemoteServiceReference ref) {
		if (ref == null)
			return false;
		IRemoteServiceID serviceID = ref.getID();
		if (serviceID == null)
			return false;
		synchronized (refToImplMap) {
			List<AbstractRemoteService> remoteServiceImplList = refToImplMap.remove(ref);
			if (remoteServiceImplList != null) {
				for (Iterator<AbstractRemoteService> i = remoteServiceImplList.iterator(); i.hasNext();) {
					AbstractRemoteService rsImpl = i.next();
					if (rsImpl != null)
						rsImpl.dispose();
					i.remove();
				}
				return true;
			}
		}
		return false;
	}

	public IRemoteFilter createRemoteFilter(String filter) throws InvalidSyntaxException {
		return new RemoteServiceFilterImpl(filter);
	}

	protected IRemoteServiceCallPolicy getRemoteServiceCallPolicy() {
		return remoteServiceCallPolicy;
	}

	protected IConnectContext getConnectContext() {
		return this.connectContext;
	}

	protected void setExecutor(IExecutor executor) {
		this.executor = executor;
	}

	protected IExecutor getExecutor() {
		return this.executor;
	}

	protected IContainer getContainer() {
		return this.container;
	}

	protected ID getLocalContainerID() {
		return getContainer().getID();
	}

	protected RemoteServiceRegistryImpl getRegistry() {
		return this.registry;
	}

	protected void setRegistry(RemoteServiceRegistryImpl registry) {
		this.registry = registry;
	}

	protected AbstractRemoteService createRemoteService(RemoteServiceRegistrationImpl registration) {
		return null;
	}

	protected IRemoteServiceRegisteredEvent createRegisteredEvent(final RemoteServiceRegistrationImpl registration) {
		return new IRemoteServiceRegisteredEvent() {

			public ID getLocalContainerID() {
				return RemoteServiceContainerAdapterImpl.this.getLocalContainerID();
			}

			public String[] getClazzes() {
				return registration.getClasses();
			}

			public ID getContainerID() {
				return registration.getContainerID();
			}

			public IRemoteServiceReference getReference() {
				return registration.getReference();
			}

			public String toString() {
				final StringBuffer buf = new StringBuffer("RemoteServiceRegisteredEvent["); //$NON-NLS-1$
				buf.append("localContainerID=").append(getLocalContainerID()); //$NON-NLS-1$
				buf.append(";containerID=").append(registration.getContainerID()); //$NON-NLS-1$
				buf.append(";clazzes=").append(Arrays.asList(registration.getClasses())); //$NON-NLS-1$
				buf.append(";reference=").append(registration.getReference()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
				return buf.toString();
			}
		};
	}

	protected void fireRemoteServiceListeners(IRemoteServiceEvent event) {
		List<IRemoteServiceListener> entries;
		synchronized (listeners) {
			entries = new ArrayList(listeners);
		}
		for (final Iterator i = entries.iterator(); i.hasNext();) {
			final IRemoteServiceListener l = (IRemoteServiceListener) i.next();
			l.handleServiceEvent(event);
		}
	}

	protected RemoteServiceRegistrationImpl createRegistration() {
		return new RemoteServiceRegistrationImpl(new IRegistrationListener() {
			public void unregister(RemoteServiceRegistrationImpl registration) {
				handleServiceUnregister(registration);
			}
		});
	}

	protected IRemoteServiceUnregisteredEvent createUnregisteredEvent(final RemoteServiceRegistrationImpl registration) {
		return new IRemoteServiceUnregisteredEvent() {

			public String[] getClazzes() {
				return registration.getClasses();
			}

			public ID getLocalContainerID() {
				return RemoteServiceContainerAdapterImpl.this.getLocalContainerID();
			}

			public ID getContainerID() {
				return registration.getContainerID();
			}

			public IRemoteServiceReference getReference() {
				return registration.getReference();
			}

			public String toString() {
				final StringBuffer buf = new StringBuffer("RemoteServiceUnregisteredEvent["); //$NON-NLS-1$
				buf.append("localContainerID=").append(getLocalContainerID()); //$NON-NLS-1$
				buf.append(";containerID=").append(registration.getContainerID()); //$NON-NLS-1$
				buf.append(";clazzes=").append(Arrays.asList(registration.getClasses())); //$NON-NLS-1$
				buf.append(";reference=").append(registration.getReference()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
				return buf.toString();
			}
		};
	}

	protected void handleServiceUnregister(RemoteServiceRegistrationImpl registration) {
		fireRemoteServiceListeners(createUnregisteredEvent(registration));
	}

	protected RemoteServiceRegistrationImpl getRemoteServiceRegistrationImpl(IRemoteServiceReference reference) {
		if (reference instanceof RemoteServiceReferenceImpl) {
			final RemoteServiceReferenceImpl ref = (RemoteServiceReferenceImpl) reference;
			if (!ref.isActive()) {
				return null;
			}
			return ref.getRegistration();
		}
		return null;
	}

	/**
	 * @param target the ID target
	 * @throws ContainerConnectException container connect exception if cannot connect
	 */
	protected void connectToRemoteServiceTarget(ID target) throws ContainerConnectException {
		// Do nothing by default
	}

	protected static String checkServiceClass(final String[] clazzes, final Object serviceObject) {
		final ClassLoader cl = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return serviceObject.getClass().getClassLoader();
			}
		});
		for (int i = 0; i < clazzes.length; i++) {
			try {
				final Class serviceClazz = cl == null ? Class.forName(clazzes[i]) : cl.loadClass(clazzes[i]);
				if (!serviceClazz.isInstance(serviceObject))
					return clazzes[i];
			} catch (final ClassNotFoundException e) {
				// This check is rarely done
				if (extensiveCheckServiceClass(clazzes[i], serviceObject.getClass()))
					return clazzes[i];
			}
		}
		return null;
	}

	private static boolean extensiveCheckServiceClass(String clazz, Class serviceClazz) {
		if (clazz.equals(serviceClazz.getName()))
			return false;
		final Class[] interfaces = serviceClazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
			if (!extensiveCheckServiceClass(clazz, interfaces[i]))
				return false;
		final Class superClazz = serviceClazz.getSuperclass();
		if (superClazz != null)
			if (!extensiveCheckServiceClass(clazz, superClazz))
				return false;
		return true;
	}

}
