/*******************************************************************************
 * Copyright (c) 2010 Florian Thienel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.vex.core.internal.dom;

import static org.junit.Assert.*;

import org.eclipse.core.runtime.QualifiedName;
import org.junit.Test;

/**
 * @author Florian Thienel
 */
public class NamespaceTest {

	@Test
	public void attributeQualifiedName() throws Exception {
		final Attribute attributeWithoutNamespace = new Attribute(null, "localName", "value");
		assertEquals("localName", attributeWithoutNamespace.getQualifiedName().toString());
		final Attribute attributeWithNamespace = new Attribute(null, new QualifiedName("http://namespace/uri", "localName"), "value");
		assertEquals("http://namespace/uri:localName", attributeWithNamespace.getQualifiedName().toString());
	}
	
	@Test
	public void declareNamespace() throws Exception {
		final Element element = new Element("element");
		assertNull(element.getNamespaceURI("nsPrefix"));
		assertNull(element.getNamespacePrefix("http://namespace/uri"));
		
		element.declareNamespace("nsPrefix", "http://namespace/uri");
		assertEquals("http://namespace/uri", element.getNamespaceURI("nsPrefix"));
		assertEquals("nsPrefix", element.getNamespacePrefix("http://namespace/uri"));
	}
	
	@Test
	public void transitiveNamespaceDeclaration() throws Exception {
		final Element parent = new Element("parent");
		final Element child = new Element("child");
		child.setParent(parent);
		assertNull(child.getNamespaceURI("nsPrefix"));
		assertNull(child.getNamespacePrefix("http://namespace/uri"));
		
		parent.declareNamespace("nsPrefix", "http://namespace/uri");
		assertEquals("http://namespace/uri", child.getNamespaceURI("nsPrefix"));
		assertEquals("nsPrefix", child.getNamespacePrefix("http://namespace/uri"));
	}
	
	@Test
	public void removeNamespaceDeclaration() throws Exception {
		final Element element = new Element("element");
		element.declareNamespace("nsPrefix", "http://namespace/uri");
		assertEquals("http://namespace/uri", element.getNamespaceURI("nsPrefix"));
		assertEquals("nsPrefix", element.getNamespacePrefix("http://namespace/uri"));
		
		element.removeNamespace("nsPrefix");
		assertNull(element.getNamespaceURI("nsPrefix"));
		assertNull(element.getNamespacePrefix("http://namespace/uri"));
	}
	
	@Test
	public void overloadNamespaceDeclaration() throws Exception {
		final Element element = new Element("element");
		element.declareNamespace("nsPrefix", "http://namespace/uri");
		element.declareNamespace("nsPrefix2", "http://namespace/uri");
		assertEquals("http://namespace/uri", element.getNamespaceURI("nsPrefix"));
		assertEquals("http://namespace/uri", element.getNamespaceURI("nsPrefix2"));
		assertNotNull(element.getNamespacePrefix("http://namespace/uri"));
	}
	
	@Test
	public void overrideNamespaceDeclaration() throws Exception {
		final Element parent = new Element("parent");
		final Element child = new Element("child");
		child.setParent(parent);
		parent.declareNamespace("nsPrefix", "http://namespace/uri");
		assertEquals("http://namespace/uri", child.getNamespaceURI("nsPrefix"));
		assertEquals("nsPrefix", child.getNamespacePrefix("http://namespace/uri"));
		
		child.declareNamespace("nsPrefix", "http://namespace2/uri");
		assertNull(child.getNamespacePrefix("http://namespace/uri"));
		assertEquals("http://namespace2/uri", child.getNamespaceURI("nsPrefix"));
		assertEquals("nsPrefix", child.getNamespacePrefix("http://namespace2/uri"));
	}
	
	@Test
	public void ignoreEmptyNamespaceURI() throws Exception {
		final Element element = new Element("element");
		element.declareNamespace("nsPrefix", null);
		assertNull(element.getNamespaceURI("nsPrefix"));
		assertNull(element.getNamespacePrefix(null));
		
		element.declareNamespace("nsPrefix", "");
		assertNull(element.getNamespaceURI("nsPrefix"));
		assertNull(element.getNamespacePrefix(""));
		
		element.declareNamespace("nsPrefix", " ");
		assertNull(element.getNamespaceURI("nsPrefix"));
		assertNull(element.getNamespacePrefix(" "));
		assertNull(element.getNamespacePrefix(""));
	}
	
	@Test
	public void handleDefaultNamespace() throws Exception {
		final Element element = new Element("element");
		assertNull(element.getDefaultNamespaceURI());
		element.declareDefaultNamespace("http://namespace/uri");
		assertEquals("http://namespace/uri", element.getDefaultNamespaceURI());
	}
}
