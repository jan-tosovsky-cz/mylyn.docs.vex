/*******************************************************************************
 * Copyright (c) 2015 Florian Thienel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.core.internal.boxes;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.vex.core.internal.dom.Document;
import org.eclipse.vex.core.internal.layout.FakeGraphics;
import org.eclipse.vex.core.internal.visualization.DocumentRootVisualization;
import org.eclipse.vex.core.internal.visualization.ParagraphVisualization;
import org.eclipse.vex.core.internal.visualization.StructureElementVisualization;
import org.eclipse.vex.core.internal.visualization.TextVisualization;
import org.eclipse.vex.core.internal.visualization.VisualizationChain;
import org.eclipse.vex.core.provisional.dom.IDocument;
import org.eclipse.vex.core.provisional.dom.IElement;
import org.eclipse.vex.core.provisional.dom.IParent;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Florian Thienel
 */
public class TestCursorPosition {

	private static final String LOREM_IPSUM_LONG = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam tincidunt congue enim, ut porta lorem lacinia consectetur.";

	private RootBox rootBox;
	private ContentMap contentMap;
	private CursorPosition cursorPosition;
	private FakeGraphics graphics;

	@Before
	public void setUp() throws Exception {
		rootBox = createTestModel();
		contentMap = new ContentMap();
		contentMap.setRootBox(rootBox);
		cursorPosition = new CursorPosition(contentMap);

		graphics = new FakeGraphics();
		rootBox.setWidth(100);
		rootBox.layout(graphics);
	}

	@Test
	public void canMoveCursorOneCharacterLeft() throws Exception {
		cursorPosition.setOffset(5);
		cursorPosition.left();
		assertEquals(4, cursorPosition.getOffset());
	}

	@Test
	public void whenAtFirstPosition_cannotMoveCursorOneCharacterLeft() throws Exception {
		cursorPosition.setOffset(0);
		cursorPosition.left();
		assertEquals(0, cursorPosition.getOffset());
	}

	@Test
	public void canMoveCursorOneCharacterRight() throws Exception {
		cursorPosition.setOffset(5);
		cursorPosition.right();
		assertEquals(6, cursorPosition.getOffset());
	}

	@Test
	public void whenAtLastPosition_cannotMoveCursorOneCharacterRight() throws Exception {
		final int lastPosition = contentMap.getLastPosition();
		cursorPosition.setOffset(lastPosition);
		cursorPosition.right();
		assertEquals(lastPosition, cursorPosition.getOffset());
	}

	@Test
	public void whenClickingIntoText_shouldMoveToPositionInText() throws Exception {
		cursorPosition.moveToAbsoluteCoordinates(graphics, 18, 11);
		assertEquals(5, cursorPosition.getOffset());
	}

	@Test
	public void whenClickingRightOfLastLine_shouldMoveToEndOfParagraph() throws Exception {
		cursorPosition.moveToAbsoluteCoordinates(graphics, 86, 400);
		assertEquals(331, cursorPosition.getOffset());
	}

	@Test
	public void whenClickingLeftOfLine_shouldMoveToBeginningOfLine() throws Exception {
		for (int x = 0; x < 10; x += 1) {
			cursorPosition.setOffset(0);
			cursorPosition.moveToAbsoluteCoordinates(graphics, x, 11);
			assertEquals("x=" + x, 4, cursorPosition.getOffset());
		}
	}

	@Test
	public void whenClickingRightOfLine_shouldMoveToEndOfLine() throws Exception {
		for (int x = 100; x > 93; x -= 1) {
			cursorPosition.setOffset(0);
			cursorPosition.moveToAbsoluteCoordinates(graphics, x, 11);
			assertEquals("x=" + x, 15, cursorPosition.getOffset());
		}
	}

	@Test
	public void whenClickingInEmptyLine_shouldMoveToEndOfParagraph() throws Exception {
		cursorPosition.moveToAbsoluteCoordinates(graphics, 10, 407);
		assertEquals(333, cursorPosition.getOffset());
	}

	@Test
	public void whenClickingBelowLastLine_shouldMoveToEndOfParagraph() throws Exception {
		for (int x = 6; x < 95; x += 1) {
			cursorPosition.setOffset(0);
			cursorPosition.moveToAbsoluteCoordinates(graphics, x, 402);
			assertEquals("x=" + x, 331, cursorPosition.getOffset());
		}
	}

	private static RootBox createTestModel() {
		final IDocument document = createTestDocument();
		final VisualizationChain visualizationChain = buildVisualizationChain();
		return visualizationChain.visualizeRoot(document);
	}

	private static VisualizationChain buildVisualizationChain() {
		final VisualizationChain visualizationChain = new VisualizationChain();
		visualizationChain.addForRoot(new DocumentRootVisualization());
		visualizationChain.addForStructure(new ParagraphVisualization());
		visualizationChain.addForStructure(new StructureElementVisualization());
		visualizationChain.addForInline(new TextVisualization());
		return visualizationChain;
	}

	private static IDocument createTestDocument() {
		final Document document = new Document(new QualifiedName(null, "doc"));
		insertSection(document.getRootElement());
		insertSection(document.getRootElement());
		return document;
	}

	private static void insertSection(final IParent parent) {
		final IElement section = insertElement(parent, "section");
		insertText(insertElement(section, "para"), LOREM_IPSUM_LONG);
		insertElement(section, "para");
	}

	private static IElement insertElement(final IParent parent, final String localName) {
		final IDocument document = parent.getDocument();
		return document.insertElement(parent.getEndOffset(), new QualifiedName(null, localName));
	}

	private static void insertText(final IParent parent, final String text) {
		final IDocument document = parent.getDocument();
		document.insertText(parent.getEndOffset(), text);
	}

	/*
	 * For visualization of the box structure: 
	 * 
	 * private void printBoxStructure() { rootBox.accept(new
	 * DepthFirstTraversal<Object>() { private String indent = "";
	 * 
	 * @Override public Object visit(final NodeReference box) { printBox(box); indent += " "; super.visit(box); indent =
	 * indent.substring(1); return null; }
	 * 
	 * @Override public Object visit(final TextContent box) { printBox(box); return null; }
	 * 
	 * private void printBox(final IContentBox box) { System.out.println(indent + "[" + box.getAbsoluteLeft() + ". " +
	 * box.getAbsoluteTop() + ", " + box.getWidth() + ", " + box.getHeight() + "] [" + box.getStartOffset() + ", " +
	 * box.getEndOffset() + "]"); } }); }
	 */
}
