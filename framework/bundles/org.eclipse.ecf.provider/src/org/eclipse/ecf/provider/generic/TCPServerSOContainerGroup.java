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
package org.eclipse.ecf.provider.generic;

import java.io.IOException;
import java.net.InetAddress;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.ECFProviderDebugOptions;
import org.eclipse.ecf.internal.provider.ProviderPlugin;
import org.eclipse.ecf.provider.comm.tcp.Server;

public class TCPServerSOContainerGroup extends SOContainerGroup {

	public static final String DEFAULT_GROUP_NAME = TCPServerSOContainerGroup.class.getName();
	private int port;
	private Server listener;
	private boolean isOnTheAir = false;
	private final ThreadGroup threadGroup;

	private int backlog = Server.DEFAULT_BACKLOG;
	private InetAddress bindAddress;

	/**
	 * @param name name
	 * @param group thread group to use
	 * @param port port
	 * @param backlog backlog
	 * @param bindAddress bind address
	 * @since 4.4
	 */
	public TCPServerSOContainerGroup(String name, ThreadGroup group, int port, int backlog, InetAddress bindAddress) {
		super(name);
		threadGroup = group;
		this.port = port;
		this.backlog = backlog;
		this.bindAddress = bindAddress;
	}

	/**
	 * @param name name
	 * @param group thread group to use
	 * @param port port
	 * @param bindAddress bind address
	 * @since 4.4
	 */
	public TCPServerSOContainerGroup(String name, ThreadGroup group, int port, InetAddress bindAddress) {
		this(name, group, port, Server.DEFAULT_BACKLOG, bindAddress);
	}

	public TCPServerSOContainerGroup(String name, ThreadGroup group, int port) {
		this(name, group, port, Server.DEFAULT_BACKLOG, null);
	}

	public TCPServerSOContainerGroup(String name, int port) {
		this(name, null, port);
	}

	public TCPServerSOContainerGroup(int port) {
		this(DEFAULT_GROUP_NAME, port);
	}

	protected void trace(String msg) {
		Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.DEBUG, "TRACING " + msg); //$NON-NLS-1$
	}

	protected void traceStack(String msg, Throwable e) {
		Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, TCPServerSOContainerGroup.class, msg, e);
	}

	public synchronized void putOnTheAir() throws IOException {
		trace("TCPServerSOContainerGroup at port " + port + " on the air"); //$NON-NLS-1$ //$NON-NLS-2$
		listener = new Server(threadGroup, port, backlog, bindAddress, this);
		port = listener.getLocalPort();
		isOnTheAir = true;
	}

	public synchronized boolean isOnTheAir() {
		return isOnTheAir;
	}

	public synchronized void takeOffTheAir() {
		if (listener != null) {
			trace("Taking " + getName() + " off the air."); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				listener.close();
			} catch (final IOException e) {
				traceStack("Exception in closeListener", e); //$NON-NLS-1$
			}
			listener = null;
		}
		isOnTheAir = false;
	}

	public int getPort() {
		return port;
	}

	public String toString() {
		return super.toString() + ";port:" + port; //$NON-NLS-1$
	}
}