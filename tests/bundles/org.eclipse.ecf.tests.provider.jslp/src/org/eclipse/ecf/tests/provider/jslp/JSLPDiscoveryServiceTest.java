/****************************************************************************
 * Copyright (c) 2007 Versant Corp.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.tests.provider.jslp;

import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.provider.jslp.container.JSLPDiscoveryContainer;
import org.eclipse.ecf.tests.discovery.DiscoveryServiceTest;

public class JSLPDiscoveryServiceTest extends DiscoveryServiceTest {

	public JSLPDiscoveryServiceTest() {
		super(JSLPDiscoveryContainer.NAME);
		setWaitTimeForProvider(JSLPDiscoveryContainer.REDISCOVER);
		
		//TODO-mkuppe https://bugs.eclipse.org/bugs/show_bug.cgi?id=230182
		setComparator(new JSLPTestComparator());
		//TODO-mkuppe https://bugs.eclipse.org/bugs/show_bug.cgi?id=218308
		setScope(IServiceTypeID.DEFAULT_SCOPE[0]);

		String[] ips;
		// tests need root privileges to bind to slp port 427 in SA mode
		try {
			String str = System.getProperty("net.slp.interfaces", "127.0.0.1");
			ips = StringUtils.split(str, ",");
		} catch (Exception e) {
			ips = new String[]{"127.0.0.1"};
		}
		setHostname(ips[0]);
	}
}
