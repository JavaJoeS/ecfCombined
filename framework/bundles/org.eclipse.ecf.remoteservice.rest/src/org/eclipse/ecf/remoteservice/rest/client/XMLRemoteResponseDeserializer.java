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
package org.eclipse.ecf.remoteservice.rest.client;

import java.io.NotSerializableException;
import java.io.StringReader;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.IRemoteResponseDeserializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * This class is a sample implementation of {@link IRemoteResponseDeserializer}. This will be
 * used to create XML Resource representations and will be registered when the
 * API is started.
 */
public class XMLRemoteResponseDeserializer implements IRemoteResponseDeserializer {

	public Object deserializeResponse(String uri, IRemoteCall call, IRemoteCallable callable, Map responseHeaders, byte[] responseBody) throws NotSerializableException {
		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
		String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
		try {
			documentFactory.setFeature(FEATURE, true);
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("ParserConfigurationException was thrown. The feature '"
		+ FEATURE + "' is not supported by your XML processor.", e);
		}
		String errorMsg = "XML response can't be parsed: "; //$NON-NLS-1$
		try {
			DocumentBuilder builder = documentFactory.newDocumentBuilder();
			InputSource src = new InputSource(new StringReader(new String(responseBody)));
			Document dom = builder.parse(src);
			return dom;
		} catch (Exception e) {
			throw new NotSerializableException(errorMsg + e.getMessage());
		}

	}

}
