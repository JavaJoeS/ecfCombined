/****************************************************************************
 * Copyright (c) 2006 Ecliptical Software Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.pubsub;

import java.util.Map;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIdentifiable;

public interface IPublishedService extends IIdentifiable {

	Map getProperties();
	
	void subscribe(ID containerID, ID requestorID);
}