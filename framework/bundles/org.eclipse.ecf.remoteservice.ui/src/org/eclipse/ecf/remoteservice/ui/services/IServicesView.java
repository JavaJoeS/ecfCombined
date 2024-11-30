/****************************************************************************
 * Copyright (c) 2015 Composent, Inc.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors: Scott Lewis - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.remoteservice.ui.services;

/**
 * @since 3.3
 */
public interface IServicesView {

	public String getRemoteId();

	public void selectService(String remoteId, long serviceId);
}
