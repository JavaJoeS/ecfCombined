/****************************************************************************
 * Copyright (c) 2008 Marcelo Mayworm.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors: 	Marcelo Mayworm - initial API and implementation
 * 
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.presence.search.message;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.util.ECFException;

/**
 * 
 * @since 2.0
 *
 */
public class MessageSearchException extends ECFException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2168236531447383111L;

	private String criteria = null;

	/**
	 * 
	 */
	public MessageSearchException(String criteria) {
		super();
		this.criteria = criteria;
	}

	/**
	 * @param status
	 */
	public MessageSearchException(IStatus status, String criteria) {
		super(status);
		this.criteria = criteria;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageSearchException(String message, Throwable cause, String criteria) {
		super(message, cause);
		this.criteria = criteria;
	}

	/**
	 * @param message
	 */
	public MessageSearchException(String message, String criteria) {
		super(message);
		this.criteria = criteria;
	}

	/**
	 * @param cause
	 */
	public MessageSearchException(Throwable cause, String criteria) {
		super(cause);
		this.criteria = criteria;
	}

	public String getSearchCriteria() {
		return this.criteria;
	}

}
