/****************************************************************************
 * Copyright (c) 2013 Composent and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Scott lewis - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.tests.osgi.services.remoteserviceadmin;

import java.util.concurrent.Future;

public interface TestServiceInterface1Async {

	Future<String> doStuff1Async();
	
}
