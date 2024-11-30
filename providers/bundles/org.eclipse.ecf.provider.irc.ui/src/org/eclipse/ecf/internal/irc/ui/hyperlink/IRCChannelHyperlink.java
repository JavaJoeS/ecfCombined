/****************************************************************************
 * Copyright (c) 2008 Abner Ballardo and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Abner Ballardo <modlost@modlost.net> - initial API and implementation via bug 197745
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.internal.irc.ui.hyperlink;

import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.chatroom.IChatRoomManager;
import org.eclipse.ecf.presence.ui.chatroom.ChatRoomManagerView;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class IRCChannelHyperlink implements IHyperlink {
	private final Region region;

	private final String channel;

	private String typeLabel;

	private String hyperlinkText;

	private final ChatRoomManagerView view;

	public IRCChannelHyperlink(ChatRoomManagerView view, String channel, Region region) {
		this.channel = channel;
		this.region = region;
		this.view = view;
	}

	public IRegion getHyperlinkRegion() {
		return this.region;
	}

	public String getHyperlinkText() {
		return this.hyperlinkText;
	}

	public String getTypeLabel() {
		return this.typeLabel;
	}

	public void open() {
		IChatRoomContainer container = view.getRootChatRoomContainer();
		final IChatRoomManager manager = (IChatRoomManager) container.getAdapter(IChatRoomManager.class);
		view.joinRoom(manager.getChatRoomInfo(channel), "");
	}

}
