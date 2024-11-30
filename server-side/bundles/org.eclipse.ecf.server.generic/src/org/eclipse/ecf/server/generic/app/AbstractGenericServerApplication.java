/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
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
package org.eclipse.ecf.server.generic.app;

import java.io.*;
import java.net.*;
import java.security.PermissionCollection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.IConnectHandlerPolicy;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerGroupManager;
import org.eclipse.ecf.provider.generic.*;

/**
 * @since 3.0
 */
public abstract class AbstractGenericServerApplication {

	protected TCPServerSOContainerGroup[] serverGroups;

	protected String configURL;
	protected String serverName;

	static class SysOutConnectHandlerPolicy implements IConnectHandlerPolicy {
		public PermissionCollection checkConnect(Object addr, ID fromID, ID targetID, String targetGroup, Object joinData) throws Exception {
			System.out.println("Client Connect Addr=" + addr + ";ID=" + fromID + ";Group=" + targetGroup); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return null;
		}

		public void refresh() {
			// do nothing
		}
	}

	static class ContainerListener implements IContainerListener {
		public void handleEvent(IContainerEvent event) {
			if (event instanceof IContainerDisconnectedEvent) {
				System.out.println("Client id=" + ((IContainerDisconnectedEvent) event).getTargetID() + " disconnected."); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (event instanceof IContainerEjectedEvent) {
				System.out.println("Client id=" + ((IContainerEjectedEvent) event).getTargetID() + " ejected."); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}

	protected void processArguments(String[] args) {
		configURL = null;
		serverName = TCPServerSOContainer.getDefaultServerURL();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-configURL")) { //$NON-NLS-1$
				configURL = args[i + 1];
				i++;
			} else if (args[i].equals("-serverName")) { //$NON-NLS-1$
				serverName = args[i + 1];
				i++;
			}
		}
	}

	protected void initialize() throws Exception {
		List connectors = null;
		if (configURL != null) {
			InputStream ins = null;
			try {
				try {
					ins = new URL(configURL).openStream();
				} catch (MalformedURLException e) {
					// If it's not an URL, just try to load via file
					ins = new FileInputStream(configURL);
				}
				// read connectors from input stream via ServerConfigParser
				connectors = new ServerConfigParser().load(ins);
			} finally {
				if (ins != null)
					ins.close();
			}
			initializeFromConnectors(connectors);
		} else
			initializeSingleServer();
	}

	protected void initializeSingleServer() throws IOException, URISyntaxException {
		java.net.URI anURL = new java.net.URI(serverName);
		int port = anURL.getPort();
		if (port == -1)
			port = TCPServerSOContainer.DEFAULT_PORT;
		String name = anURL.getPath();
		if (name == null)
			name = TCPServerSOContainer.DEFAULT_NAME;
		serverGroups = new TCPServerSOContainerGroup[1];
		// Setup server group
		serverGroups[0] = new TCPServerSOContainerGroup(anURL.getPort());
		TCPServerSOContainer server = createServerContainer(serverName, serverGroups[0], name, TCPServerSOContainer.DEFAULT_KEEPALIVE);

		setupServerContainer(server);
		// Then put the new server on the air
		serverGroups[0].putOnTheAir();
	}

	protected void initializeFromConnectors(List connectors) throws IOException {
		if (connectors == null)
			return;
		serverGroups = new TCPServerSOContainerGroup[connectors.size()];
		int j = 0;
		for (Iterator i = connectors.iterator(); i.hasNext();) {
			Connector connector = (Connector) i.next();
			serverGroups[j] = new TCPServerSOContainerGroup(connector.getHostname(), connector.getPort());
			List groups = connector.getGroups();
			for (Iterator g = groups.iterator(); g.hasNext();) {
				NamedGroup group = (NamedGroup) g.next();
				TCPServerSOContainer container = createServerContainer(group.getIDForGroup(), serverGroups[j], group.getName(), connector.getTimeout());
				setupServerContainer(container);
			}
			serverGroups[j].putOnTheAir();
			j++;
		}
	}

	protected TCPServerSOContainer createServerContainer(String id, TCPServerSOContainerGroup group, String path, int keepAlive) throws IDCreateException {
		return new TCPServerSOContainer(new SOContainerConfig(IDFactory.getDefault().createStringID(id)), group, path, keepAlive);
	}

	protected void setupServerContainer(TCPServerSOContainer container) {
		// Setup connect policy
		((ISharedObjectContainerGroupManager) container).setConnectPolicy(new SysOutConnectHandlerPolicy());
		// Setup container listener
		container.addListener(new ContainerListener());
	}

	protected void shutdown() {
		if (serverGroups != null) {
			for (int i = 0; i < serverGroups.length; i++) {
				serverGroups[i].takeOffTheAir();
				final Iterator iter = serverGroups[i].elements();
				for (; iter.hasNext();) {
					((TCPServerSOContainer) iter.next()).dispose();
				}
			}
			serverGroups = null;
		}
	}
}
