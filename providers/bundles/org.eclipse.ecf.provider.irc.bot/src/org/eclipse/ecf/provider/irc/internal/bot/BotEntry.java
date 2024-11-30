/****************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.provider.irc.internal.bot;

import java.util.List;

public class BotEntry implements IBotEntry {

	private String id;
	private String name;
	private String server;
	private String channel;
	private List commands;
	
	public BotEntry(String id, String name, String server, String channel, List commands) {
		this.id = id;
		this.name = name;
		this.server = server;
		this.channel = channel;
		this.commands = commands;
	}
	
	public String getChannel() {
		return channel;
	}

	public List getCommands() {
		return commands;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getServer() {
		return server;
	}

}
