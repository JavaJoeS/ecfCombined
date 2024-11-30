/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.examples.loadbalancing;

import org.eclipse.ecf.remoteservice.IAsyncCallback;

public interface IDataProcessorAsync {

	public void processDataAsync(String data, IAsyncCallback<String> callback);

}
