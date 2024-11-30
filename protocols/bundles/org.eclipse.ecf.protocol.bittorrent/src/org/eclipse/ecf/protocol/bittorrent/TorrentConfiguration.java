/****************************************************************************
 * Copyright (c) 2006, 2008 Remy Suen, Composent Inc., and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Remy Suen <remy.suen@gmail.com> - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.protocol.bittorrent;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.ecf.protocol.bittorrent.internal.net.TorrentManager;

/**
 * A class used to define properties and configurations such as specifying where
 * a torrent's state information should be stored and how debugging messages
 * should be displayed.
 */
public final class TorrentConfiguration {

	/**
	 * A boolean flag to specify whether debugging output should be printed or
	 * not.
	 */
	public static boolean DEBUG = false;

	/**
	 * The folder in which all state and configuration information should be
	 * saved.
	 */
	static File statePath = null;

	/**
	 * The shared <code>Calendar</code> instance that will be used to retrieve
	 * the current time for debugging output.
	 * 
	 * @see #DEBUG
	 * @see #debug(String)
	 */
	private static final Calendar calendar = Calendar.getInstance();

	/**
	 * The implementation of {@link IDebugListener} that's currently monitoring
	 * debug messages.
	 */
	private static IDebugListener debugListener;

	/**
	 * This method is called by classes that wishes to log a message to the
	 * {@link IDebugListener} that was set with
	 * {@link #setDebugListener(org.eclipse.ecf.protocol.bittorrent.TorrentConfiguration.IDebugListener)}.
	 * 
	 * @param message
	 *            the message to print out
	 */
	public static void debug(String message) {
		if (DEBUG && debugListener != null) {
			Date date = new Date(System.currentTimeMillis());
			calendar.setTime(date);
			int hour = calendar.get(Calendar.HOUR);
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			String hourString = (hour < 10 ? "0" + hour : Integer //$NON-NLS-1$
					.toString(hour));
			String minuteString = (minute < 10 ? "0" + minute : Integer //$NON-NLS-1$
					.toString(minute));
			String secondString = second < 10 ? "0" + second : Integer //$NON-NLS-1$
					.toString(second);
			debugListener.print(hourString + ":" + minuteString + ":" //$NON-NLS-1$ //$NON-NLS-2$
					+ secondString + " " + message); //$NON-NLS-1$
		}
	}

	/**
	 * Sets the listener that will be notified of debugging messages.
	 * 
	 * @param listener
	 *            the listener to use, or <code>null</code> if the previously
	 *            set listener should be discarded
	 */
	public static void setDebugListener(IDebugListener listener) {
		debugListener = listener;
	}

	/**
	 * Sets the directory to use to save configuration and status information
	 * when starting up torrents. Note that the configuration path cannot be
	 * modified again with a second invocation of this method.
	 * 
	 * @param directory
	 *            the directory to save the states into
	 * @throws IllegalArgumentException
	 *             If <code>directory</code> is <code>null</code>, a file
	 *             and not a directory, cannot be written, or could not be
	 *             created
	 */
	public static void setConfigurationPath(File directory) {
		if (statePath != null) {
			return;
		} else if (directory == null) {
			throw new IllegalArgumentException("The provided directory cannot be null"); //$NON-NLS-1$
		} else if (directory.isFile()) {
			throw new IllegalArgumentException(directory.getAbsolutePath()
					+ " is a file"); //$NON-NLS-1$
		} else if (!directory.exists() && !directory.mkdirs()) {
			throw new IllegalArgumentException("The directory " //$NON-NLS-1$
					+ directory.getAbsolutePath() + " could not be made"); //$NON-NLS-1$
		} else if (!directory.canWrite()) {
			throw new IllegalArgumentException(directory.getAbsolutePath()
					+ " cannot be written to"); //$NON-NLS-1$
		}
		statePath = directory;
		TorrentManager.setStatePath(statePath);
	}

	/**
	 * Removes saved information regarding a torrent's progress and status based
	 * on its hexadecimal hash value.
	 * 
	 * @param hexHash
	 *            the hexadecimal hash value of the torrent
	 * @throws IllegalArgumentException
	 *             If <code>hexHash</code> is <code>null</code>
	 */
	public static void remove(String hexHash) {
		if (hexHash == null) {
			throw new IllegalArgumentException("The hash cannot be null"); //$NON-NLS-1$
		}
		Torrent torrent = TorrentServer.remove(hexHash);
		if (torrent != null) {
			torrent.remove();
		} else {
			File[] files = statePath.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().startsWith(hexHash)) {
					files[i].delete();
				}
			}
		}
	}

	/**
	 * A private constructor to prevent user instantiation.
	 */
	private TorrentConfiguration() {
		// do nothing
	}

	/**
	 * An interface to setup an outlet for debugging messages. Instances of this
	 * class should be set with the
	 * {@link TorrentConfiguration#setDebugListener(TorrentConfiguration.IDebugListener)}
	 * method.
	 */
	public interface IDebugListener {

		/**
		 * This method is called when a debugging message has been relayed.
		 * Messages will not be sent unless {@link TorrentConfiguration#DEBUG}
		 * is set to <code>true</code>.
		 * 
		 * @param message
		 *            the debugging message
		 */
		public void print(String message);

	}

}
