/****************************************************************************
 * Copyright (c) Delmarva Security
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.internal.ssl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Optional;
import javax.net.ssl.*;

public enum ECFSSLContext {
	SETUP;

	public static SSLContext get() {
		SSLContext context = null;
		System.out.println("ECFSSLContext  get-------------------early now");
		try {

			context = SSLContextHelper.getSSLContext("TLS");

			System.out.println("ECFSSLContext  got-------------------SSLContextHelper now");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ECFSSLContext  get---EXCEEPTION");
		}
		System.out.println("ECFSSLContext  get-------------------CHECK OPTIONALS");
		Optional<String> keystoreContainer = null;
		Optional<String> truststoreContainer = null;
		Optional<String> keystoreContainerPw = null;
		Optional<String> keystoreContainerType = null;
		Optional<String> truststoreContainerPw = null;
		Optional<String> truststoreContainerType = null;

		KeyStore keyStore = null;
		KeyStore trustStore = null;

		try {
			keystoreContainer = Optional.ofNullable(System.getProperty("javax.net.ssl.keyStore"));//$NON-NLS-1$
			keystoreContainerType = Optional.ofNullable(System.getProperty("javax.net.ssl.keyStoreType"));//$NON-NLS-1$
			keystoreContainerPw = Optional.ofNullable(System.getProperty("javax.net.ssl.keyStorePassword"));//$NON-NLS-1$

			truststoreContainer = Optional.ofNullable(System.getProperty("javax.net.ssl.trustStore"));//$NON-NLS-1$
			truststoreContainerPw = Optional.ofNullable(System.getProperty("javax.net.ssl.trustStorePassword"));//$NON-NLS-1$
			truststoreContainerType = Optional.ofNullable(System.getProperty("javax.net.ssl.trustStoreType"));//$NON-NLS-1$

			// Ensure all the properties are set for a complete PKI
			if ((keystoreContainer.isEmpty()) || (truststoreContainer.isEmpty()) || (keystoreContainerPw.isEmpty())
					|| (keystoreContainerType.isEmpty()) || (truststoreContainerPw.isEmpty())
					|| (truststoreContainerType.isEmpty())) {
				System.out.println("ECFSSLContext  get---NO PROPERTIES CONFIGURED");
				// Returns the context from the SSLContextHelper
				return context;
			}

			String keyStoreLocation = (String) keystoreContainer.get();
			String keyStoreType = (String) keystoreContainerType.get();
			String keyStorePassword = (String) keystoreContainerPw.get();

			String trustStoreLocation = (String) truststoreContainer.get();
			String trustStorePassword = (String) truststoreContainerPw.get();
			String trustStoreType = (String) truststoreContainerType.get();

			try {
				System.out.println("ECFSSLContext  SSLContext CONFIGURING");
				InputStream is = Files.newInputStream(Paths.get(keyStoreLocation));
				keyStore = KeyStore.getInstance(keyStoreType);
				keyStore.load(is, keyStorePassword.toCharArray());
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(keyStore, keyStorePassword.toCharArray());
				context = SSLContext.getInstance("TLS");

				InputStream tis = Files.newInputStream(Paths.get(trustStoreLocation));
				trustStore = KeyStore.getInstance(trustStoreType);
				trustStore.load(tis, trustStorePassword.toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
				tmf.init(trustStore);

				context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

				SSLContext.setDefault(context);
				System.out.println("ECFSSLContext  SSLContext default PROPERTIES CONFIGURED");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ECFSSLContext ------------  DONE");
		return context;
	}

}
