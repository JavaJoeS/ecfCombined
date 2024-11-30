/****************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.tests.discovery;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.discovery.IDiscoveryAdvertiser;
import org.eclipse.ecf.discovery.IDiscoveryLocator;

public abstract class DiscoveryContainerTest extends DiscoveryTest {
	protected IContainer container = null;

	public DiscoveryContainerTest(String name) {
		super(name);
	}
	
	protected IDiscoveryLocator getDiscoveryLocator() {
		final IDiscoveryLocator adapter = (IDiscoveryLocator) container.getAdapter(IDiscoveryLocator.class);
		assertNotNull("Adapter must not be null", adapter);
		return adapter;
	}

	protected IDiscoveryAdvertiser getDiscoveryAdvertiser() {
		final IDiscoveryAdvertiser adapter = (IDiscoveryAdvertiser) container.getAdapter(IDiscoveryAdvertiser.class);
		assertNotNull("Adapter must not be null", adapter);
		return adapter;
	}

	protected IContainer getContainer(String containerUnderTest) throws ContainerCreateException {
		return ContainerFactory.getDefault().createContainer(containerUnderTest);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.DiscoveryTest#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		container.disconnect();
		container.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.discovery.AbstractDiscoveryTest#setUp()
	 */
	protected void setUp() throws Exception {
		container = getContainer(containerUnderTest);
		assertNotNull(container);
		container.connect(null, null);
		super.setUp();
	}

}
