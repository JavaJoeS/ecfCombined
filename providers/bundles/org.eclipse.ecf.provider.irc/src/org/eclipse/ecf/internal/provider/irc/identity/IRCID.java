/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
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
package org.eclipse.ecf.internal.provider.irc.identity;

import java.net.URI;
import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.presence.im.IChatID;

public class IRCID extends BaseID implements IChatID {
	private static final long serialVersionUID = -6582811574473106742L;
	public static final int DEFAULT_IRC_PORT = 6667;

	URI uri;

	protected IRCID(Namespace namespace, URI uri) {
		super(namespace);
		this.uri = uri;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceCompareTo(org.eclipse.ecf.core.identity.BaseID)
	 */
	protected int namespaceCompareTo(BaseID o) {
		return getName().compareTo(o.getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceEquals(org.eclipse.ecf.core.identity.BaseID)
	 */
	protected boolean namespaceEquals(BaseID o) {
		if (!(o instanceof IRCID)) {
			return false;
		}
		IRCID other = (IRCID) o;
		return uri.equals(other.uri);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceGetName()
	 */
	protected String namespaceGetName() {
		return uri.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceHashCode()
	 */
	protected int namespaceHashCode() {
		return uri.hashCode();
	}

	public String getUser() {
		return uri.getUserInfo();
	}

	public String getHost() {
		return uri.getHost();
	}

	public String getHostname() {
		return getHost();
	}

	public int getPort() {
		int p = uri.getPort();
		if (p == -1)
			return DEFAULT_IRC_PORT;
		return p;
	}

	public String getChannel() {
		String frag = uri.getFragment();
		if (frag != null && !frag.equals(""))return "#" + frag; //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("IRCID["); //$NON-NLS-1$
		sb.append(uri.toString()).append(']');
		return sb.toString();
	}

	public String getUsername() {
		return getUser();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.BaseID#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class clazz) {
		if (clazz.isInstance(this)) {
			return this;
		}
		return super.getAdapter(clazz);
	}
}
