/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
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

package org.eclipse.ecf.provider.remoteservice.generic;

import java.io.Serializable;
import java.security.AccessControlException;
import java.util.Vector;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.ID;

public class AddRegistrationRequest implements Serializable {
	private static final long serialVersionUID = -2671778516104780091L;
	ID targetID;
	String service;
	String filter;
	AccessControlException acc;
	AddRegistrationRequest parent;

	private boolean done = false;

	/**
	 * @param targetID targetID
	 * @param service service
	 * @param filter filter
	 * @param parent parent request
	 * @since 3.0
	 */
	public AddRegistrationRequest(ID targetID, String service, String filter, AddRegistrationRequest parent) {
		this.targetID = null;
		this.parent = null;
		this.service = service;
		this.filter = filter;
	}

	private transient Vector requests = null;

	/**
	 * @param service service
	 * @param filter filter
	 * @param requests other requests
	 * @since 3.3
	 */
	@SuppressWarnings("unchecked")
	public AddRegistrationRequest(String service, String filter, Vector requests) {
		this.parent = null;
		this.targetID = null;
		Assert.isNotNull(service);
		this.service = service;
		this.filter = filter;
		this.requests = requests;
		if (requests != null)
			requests.add(this);
	}

	public String getService() {
		return service;
	}

	public String getFilter() {
		return filter;
	}

	public Integer getId() {
		return Integer.valueOf(System.identityHashCode(this));
	}

	/**
	 * @param timeout timeout
	 * @since 3.3
	 */
	public void waitForResponse(long timeout) {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeout;
		synchronized (this) {
			while (!done && (endTime >= System.currentTimeMillis())) {
				try {
					wait(timeout / 10);
				} catch (InterruptedException e) {
					// just return;
					return;
				}
			}
		}
	}

	void waitForAllResponses(long timeout) {
		if (requests != null) {
			long startTime = System.currentTimeMillis();
			long endTime = startTime + timeout;
			synchronized (requests) {
				while (requests.size() > 0 && (endTime >= System.currentTimeMillis())) {
					try {
						requests.wait(timeout / 10);
					} catch (InterruptedException e) {
						// just return
						return;
					}
				}
			}
		}
	}

	void notifyResponse(AccessControlException exception) {
		if (requests != null) {
			synchronized (requests) {
				requests.remove(this);
				requests.notify();
			}
		}
	}

	public boolean isDone() {
		return done;
	}

	public AccessControlException getException() {
		return acc;
	}

	/**
	 * @param from from ID
	 * @param exception exception
	 * @since 3.0
	 */
	public void notifyResponse(ID from, AccessControlException exception) {
		if (targetID == null || targetID.equals(from)) {
			this.acc = exception;
			synchronized (this) {
				done = true;
				if (parent != null) {
					parent.notifyResponse(from, exception);
				} else {
					synchronized (this) {
						this.notify();
					}
				}
			}
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("AddRegistrationRequest["); //$NON-NLS-1$
		buf.append("service=").append(service).append(";filter=").append(filter).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return buf.toString();
	}
}
