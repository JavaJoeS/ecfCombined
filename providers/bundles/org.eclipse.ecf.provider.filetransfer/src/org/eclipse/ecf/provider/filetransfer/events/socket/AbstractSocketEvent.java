/****************************************************************************
 * Copyright (c) 2009 IBM, and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *   IBM Corporation - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/

package org.eclipse.ecf.provider.filetransfer.events.socket;

import java.net.Socket;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEventSource;

public abstract class AbstractSocketEvent implements ISocketEvent {
	private Socket factorySocket;
	private Socket wrappedSocket;
	private ISocketEventSource source;

	protected AbstractSocketEvent(ISocketEventSource source, Socket factorySocket, Socket wrappedSocket) {
		Assert.isNotNull(source);
		Assert.isNotNull(factorySocket);
		Assert.isNotNull(wrappedSocket);
		this.source = source;
		this.factorySocket = factorySocket;
		this.wrappedSocket = wrappedSocket;
	}

	public ISocketEventSource getSource() {
		return source;
	}

	public Socket getFactorySocket() {
		return factorySocket;
	}

	public boolean isSameFactorySocket(ISocketEvent event) {
		AbstractSocketEvent other = (AbstractSocketEvent) event;
		return this.getFactorySocket() == other.getFactorySocket();
	}

	public Socket getSocket() {
		return wrappedSocket;
	}

	protected void setSocket(Socket socket) {
		this.wrappedSocket = socket;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(getEventName() + "["); //$NON-NLS-1$
		sb.append("source="); //$NON-NLS-1$
		sb.append(source);
		sb.append(" socket="); //$NON-NLS-1$
		sb.append(getSocket());
		sb.append(']');
		return sb.toString();
	}

	protected abstract String getEventName();

}
