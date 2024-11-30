/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors: Composent, Inc. - initial API and implementation
 *
 * SPDX-License-Identifier: EPL-2.0
 *****************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import java.util.List;

/**
 * Container factory contract {@link SharedObjectFactory} for default
 * implementation.
 */
public interface ISharedObjectFactory {
	/*
	 * Add a SharedObjectTypeDescription to the set of known
	 * SharedObjectTypeDescriptions.
	 * 
	 * @param scd the SharedObjectTypeDescription to add to this factory @return
	 * SharedObjectTypeDescription the old description of the same name, null if
	 * none found
	 */
	public SharedObjectTypeDescription addDescription(
			SharedObjectTypeDescription description);

	/**
	 * Get a collection of the SharedObjectTypeDescriptions currently known to
	 * this factory. This allows clients to query the factory to determine what
	 * if any other SharedObjectTypeDescriptions are currently registered with
	 * the factory, and if so, what they are.
	 * 
	 * @return List of SharedObjectTypeDescription instances. Will not be null.
	 */
	public List getDescriptions();

	/**
	 * Check to see if a given named description is already contained by this
	 * factory
	 * 
	 * @param description
	 *            the SharedObjectTypeDescription to look for
	 * @return true if description is already known to factory, false otherwise
	 */
	public boolean containsDescription(SharedObjectTypeDescription description);

	/**
	 * Get the known SharedObjectTypeDescription given it's name.
	 * 
	 * @param name
	 * @return SharedObjectTypeDescription found. Null if description not found.
	 * @throws SharedObjectCreateException
	 */
	public SharedObjectTypeDescription getDescriptionByName(String name)
			throws SharedObjectCreateException;

	/**
	 * Create ISharedObject instance. Given a SharedObjectTypeDescription
	 * object, a String [] of argument types, and an Object [] of parameters,
	 * this method will
	 * <p>
	 * <ul>
	 * <li>lookup the known SharedObjectTypeDescriptions to find one of
	 * matching name</li>
	 * <li>if found, will retrieve or create an ISharedObjectInstantiator for
	 * that description</li>
	 * <li>Call the ISharedObjectInstantiator.createInstance method to return
	 * an instance of ISharedObject</li>
	 * </ul>
	 * 
	 * @param typeDescription
	 *            the SharedObjectTypeDescription to use to create the instance
	 * @param args
	 *            an Object [] of arguments passed to the createInstance method
	 *            of the ISharedObjectInstantiator
	 * @return a valid instance of ISharedObject. Will not be null.
	 * @throws SharedObjectCreateException
	 *             if shared object cannot be created
	 */
	public ISharedObject createSharedObject(
			SharedObjectTypeDescription typeDescription, Object[] args)
			throws SharedObjectCreateException;

	/**
	 * Create ISharedObject instance. Given a SharedObjectTypeDescription name,
	 * this method will
	 * <p>
	 * <ul>
	 * <li>lookup the known SharedObjectTypeDescriptions to find one of
	 * matching name</li>
	 * <li>if found, will retrieve or create an ISharedObjectInstantiator for
	 * that description</li>
	 * <li>Call the ISharedObjectInstantiator.createInstance method to return
	 * an instance of ISharedObject</li>
	 * </ul>
	 * 
	 * @param descriptionName
	 *            the SharedObjectTypeDescription name to lookup
	 * @return a valid instance of ISharedObject. Will not be null.
	 * @throws SharedObjectCreateException
	 */
	public ISharedObject createSharedObject(String descriptionName)
			throws SharedObjectCreateException;

	/**
	 * Create ISharedObject instance. Given a SharedObjectTypeDescription name,
	 * this method will
	 * <p>
	 * <ul>
	 * <li>lookup the known SharedObjectTypeDescriptions to find one of
	 * matching name</li>
	 * <li>if found, will retrieve or create an ISharedObjectInstantiator for
	 * that description</li>
	 * <li>Call the ISharedObjectInstantiator.createInstance method to return
	 * an instance of ISharedObject</li>
	 * </ul>
	 * 
	 * @param descriptionName
	 *            the SharedObjectTypeDescription name to lookup
	 * @param args
	 *            the Object [] of arguments passed to the
	 *            ISharedObjectInstantiator.createInstance method
	 * @return a valid instance of IContainer. Will not be null.
	 * @throws SharedObjectCreateException
	 */
	public ISharedObject createSharedObject(String descriptionName,
			Object[] args) throws SharedObjectCreateException;

	/**
	 * Remove given description from set known to this factory.
	 * 
	 * @param scd
	 *            the SharedObjectTypeDescription to remove
	 * @return the removed SharedObjectTypeDescription, null if nothing removed
	 */
	public SharedObjectTypeDescription removeDescription(
			SharedObjectTypeDescription scd);
}