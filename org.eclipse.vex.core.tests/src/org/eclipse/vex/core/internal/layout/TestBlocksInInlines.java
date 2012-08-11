/*******************************************************************************
 * Copyright (c) 2004, 2008 John Krasnay and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     John Krasnay - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.core.internal.layout;

import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.vex.core.internal.core.DisplayDevice;
import org.eclipse.vex.core.internal.css.MockDisplayDevice;
import org.eclipse.vex.core.internal.css.StyleSheet;
import org.eclipse.vex.core.internal.css.StyleSheetReader;
import org.eclipse.vex.core.internal.dom.Document;
import org.eclipse.vex.core.internal.dom.Element;
import org.eclipse.vex.core.internal.dom.RootElement;

/**
 * Tests proper function of a block-level element within an inline element. These must be layed out as a block child of
 * the containing block element.
 */
public class TestBlocksInInlines extends TestCase {

	FakeGraphics g;
	LayoutContext context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DisplayDevice.setCurrent(new MockDisplayDevice(90, 90));
	}

	public TestBlocksInInlines() throws Exception {
		final URL url = this.getClass().getResource("test.css");
		final StyleSheetReader reader = new StyleSheetReader();
		final StyleSheet ss = reader.read(url);

		g = new FakeGraphics();

		context = new LayoutContext();
		context.setBoxFactory(new MockBoxFactory());
		context.setGraphics(g);
		context.setStyleSheet(ss);
	}

	public void testBlockInInline() throws Exception {
		final RootElement root = new RootElement("root");
		final Document doc = new Document(root);
		context.setDocument(doc);

		doc.insertText(1, "one  five");
		doc.insertElement(5, new Element("b"));
		doc.insertText(6, "two  four");
		doc.insertElement(10, new Element("p"));
		doc.insertText(11, "three");

		final RootBox rootBox = new RootBox(context, root, 500);
		rootBox.layout(context, 0, Integer.MAX_VALUE);

	}
}
