/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *               Cloudsmith, Inc. - additional API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/

package org.eclipse.ecf.tests.filetransfer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.filetransfer.FileTransferJob;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.filetransfer.identity.IFileID;

public class URLRetrieveTestWithCustomJob extends AbstractRetrieveTestCase {

	private static final String HTTP_RETRIEVE = "http://www.eclipse.org/ecf/ip_log.html";
	protected static final String HTTPS_RETRIEVE = "https://www.eclipse.org/";

	File tmpFile = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		tmpFile = Files.createTempFile("ECFTest", "").toFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		if (tmpFile != null)
			tmpFile.delete();
		tmpFile = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractRetrieveTestCase#handleStartEvent(org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent)
	 */
	protected void handleStartEvent(IIncomingFileTransferReceiveStartEvent event) {
		super.handleStartEvent(event);
		assertNotNull(event.getFileID());
		assertNotNull(event.getFileID().getFilename());
		try {
			incomingFileTransfer = event.receive(tmpFile, fileTransferJob);
		} catch (final IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

	FileTransferJob fileTransferJob;

	protected void testReceive(String url) throws Exception {
		assertNotNull(retrieveAdapter);
		final IFileTransferListener listener = createFileTransferListener();
		final IFileID fileID = createFileID(new URL(url));

		fileTransferJob = new FileTransferJob(fileID.getName());
		fileTransferJob.addJobChangeListener(new JobChangeTraceListener(startTime));
		retrieveAdapter.sendRetrieveRequest(fileID, listener, null);

		waitForDone(20000);

		final IStatus result = fileTransferJob.getResult();
		System.out.println("job=" + fileTransferJob.getName() + " result=" + result);

		assertTrue(result != null);
		assertHasEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
		assertHasMoreThanEventCount(dataEvents, IIncomingFileTransferReceiveDataEvent.class, 0);
		assertDoneOK();

		assertTrue(tmpFile.exists());
		assertTrue(tmpFile.length() > 0);
	}

	public void testReceiveFile() throws Exception {
		testReceive(HTTP_RETRIEVE);
	}

	public void testHttpsReceiveFile() throws Exception {
		testReceive(HTTPS_RETRIEVE);
	}
}
