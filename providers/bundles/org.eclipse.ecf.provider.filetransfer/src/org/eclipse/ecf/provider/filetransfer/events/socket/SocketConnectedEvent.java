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
import org.eclipse.ecf.filetransfer.events.socket.ISocketConnectedEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEventSource;

public class SocketConnectedEvent extends AbstractSocketEvent implements ISocketConnectedEvent {

	public SocketConnectedEvent(ISocketEventSource source, Socket factorySocket, Socket wrappedSocket) {
		super(source, factorySocket, wrappedSocket);
	}

	protected String getEventName() {
		return "SocketConnectedEvent"; //$NON-NLS-1$
	}

	public void setSocket(Socket socket) {
		super.setSocket(socket);
	}

}
