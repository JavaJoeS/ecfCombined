/****************************************************************************
 * Copyright (c) 2009 EclipseSource and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.provider.filetransfer.httpclient4;

/**
 * @since 4.0
 */
public interface HttpClientOptions {
	// HttpClient response code that indicates that NTLM proxy is asking for authentication
	// and httpclient cannot handle NTLMv2 proxies
	public int NTLM_PROXY_RESPONSE_CODE = 477;
	// System property that indicates that NTLM proxy usage should be forced (i.e. not rejected)
	// The property key is:  org.eclipse.ecf.provider.filetransfer.httpclient4.options.ForceNTLMProxy
	// The value of the property must be non-null, but is not otherwise used.
	public String FORCE_NTLM_PROP = "org.eclipse.ecf.provider.filetransfer.httpclient4.options.ForceNTLMProxy"; //$NON-NLS-1$

	// changing to 2 minutes (120000) as per bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=266246
	// 10/26/2009:  Added being able to set with system property with name org.eclipse.ecf.provider.filetransfer.httpclient4.retrieve.connectTimeout
	// for https://bugs.eclipse.org/bugs/show_bug.cgi?id=292995
	/**
	 * @since 4.0
	 */
	public static final int RETRIEVE_DEFAULT_CONNECTION_TIMEOUT = Integer.parseInt(System.getProperty("org.eclipse.ecf.provider.filetransfer.httpclient4.retrieve.connectTimeout", "120000")); //$NON-NLS-1$ //$NON-NLS-2$
	// changing to 2 minutes (120000) as per bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=266246
	// 10/26/2009:  Added being able to set with system property with name org.eclipse.ecf.provider.filetransfer.httpclient4.retrieve.readTimeout
	// for https://bugs.eclipse.org/bugs/show_bug.cgi?id=292995
	/**
	 * @since 4.0
	 */
	public static final int RETRIEVE_DEFAULT_READ_TIMEOUT = Integer.parseInt(System.getProperty("org.eclipse.ecf.provider.filetransfer.httpclient4.retrieve.readTimeout", "120000")); //$NON-NLS-1$ //$NON-NLS-2$

	// changing to 2 minutes (120000) as per bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=266246
	// 10/26/2009:  Added being able to set with system property with name org.eclipse.ecf.provider.filetransfer.httpclient4.browse.connectTimeout
	// for https://bugs.eclipse.org/bugs/show_bug.cgi?id=292995
	/**
	 * @since 4.0
	 */
	public static final int BROWSE_DEFAULT_CONNECTION_TIMEOUT = Integer.parseInt(System.getProperty("org.eclipse.ecf.provider.filetransfer.httpclient4.browse.connectTimeout", "120000")); //$NON-NLS-1$ //$NON-NLS-2$;

}
