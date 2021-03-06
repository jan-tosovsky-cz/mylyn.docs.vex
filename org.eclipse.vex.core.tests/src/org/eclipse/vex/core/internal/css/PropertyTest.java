/*******************************************************************************
 * Copyright (c) 2004, 2013 John Krasnay and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Krasnay - initial API and implementation
 *     Carsten Hieserich - added test for OutlineContentProperty
 *******************************************************************************/
package org.eclipse.vex.core.internal.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.eclipse.vex.core.internal.core.DisplayDevice;
import org.eclipse.vex.core.internal.io.DocumentReader;
import org.eclipse.vex.core.provisional.dom.IDocument;
import org.eclipse.vex.core.provisional.dom.IElement;
import org.eclipse.vex.core.provisional.dom.IProcessingInstruction;
import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;

public class PropertyTest {

	/**
	 * From CSS2.1 section 8.5.3
	 */
	@Test
	public void testBorderStyleProperty() throws Exception {
		final Styles styles = new Styles();
		final Styles parentStyles = new Styles();
		final IProperty prop = new BorderStyleProperty(CSS.BORDER_TOP_STYLE);

		// Inheritance
		parentStyles.put(CSS.BORDER_TOP_STYLE, CSS.DASHED);
		assertEquals(CSS.NONE, prop.calculate(null, parentStyles, styles, null));
		assertEquals(CSS.DASHED, prop.calculate(MockLU.INHERIT, parentStyles, styles, null)); // not inherited

		// Regular values
		assertEquals(CSS.NONE, prop.calculate(MockLU.createIdent(CSS.NONE), parentStyles, styles, null));
		assertEquals(CSS.HIDDEN, prop.calculate(MockLU.createIdent(CSS.HIDDEN), parentStyles, styles, null));
		assertEquals(CSS.DOTTED, prop.calculate(MockLU.createIdent(CSS.DOTTED), parentStyles, styles, null));
		assertEquals(CSS.DASHED, prop.calculate(MockLU.createIdent(CSS.DASHED), parentStyles, styles, null));
		assertEquals(CSS.SOLID, prop.calculate(MockLU.createIdent(CSS.SOLID), parentStyles, styles, null));
		assertEquals(CSS.DOUBLE, prop.calculate(MockLU.createIdent(CSS.DOUBLE), parentStyles, styles, null));
		assertEquals(CSS.GROOVE, prop.calculate(MockLU.createIdent(CSS.GROOVE), parentStyles, styles, null));
		assertEquals(CSS.RIDGE, prop.calculate(MockLU.createIdent(CSS.RIDGE), parentStyles, styles, null));
		assertEquals(CSS.INSET, prop.calculate(MockLU.createIdent(CSS.INSET), parentStyles, styles, null));
		assertEquals(CSS.OUTSET, prop.calculate(MockLU.createIdent(CSS.OUTSET), parentStyles, styles, null));

		// Invalid token
		assertEquals(CSS.NONE, prop.calculate(MockLU.createIdent(CSS.BOLD), parentStyles, styles, null));

		// Wrong type
		assertEquals(CSS.NONE, prop.calculate(MockLU.createString(CSS.HIDDEN), parentStyles, styles, null));
	}

	/**
	 * From CSS2.1 section 8.5.1
	 */
	@Test
	public void testBorderWidthProperty() throws Exception {

		final Styles styles = new Styles();
		final Styles parentStyles = new Styles();
		DisplayDevice.setCurrent(new DummyDisplayDevice(50, 100));
		IProperty prop = new BorderWidthProperty(CSS.BORDER_TOP_WIDTH, CSS.BORDER_TOP_STYLE, IProperty.Axis.VERTICAL);

		styles.put(CSS.FONT_SIZE, new Float(12));
		styles.put(CSS.BORDER_TOP_STYLE, CSS.SOLID);

		// Inheritance
		parentStyles.put(CSS.BORDER_TOP_WIDTH, new Integer(27));
		assertEquals(new Integer(3), prop.calculate(null, parentStyles, styles, null));
		assertEquals(new Integer(27), prop.calculate(MockLU.INHERIT, parentStyles, styles, null)); // not inherited

		// Regular values
		assertEquals(new Integer(20), prop.calculate(MockLU.createFloat(LexicalUnit.SAC_INCH, 0.2f), parentStyles, styles, null));

		// Invalid token
		assertEquals(new Integer(3), prop.calculate(MockLU.createIdent(CSS.BOLD), parentStyles, styles, null));

		// Wrong type
		assertEquals(new Integer(3), prop.calculate(MockLU.createString(CSS.HIDDEN), parentStyles, styles, null));

		// Corresponding style is "none" or "hidden"
		styles.put(CSS.BORDER_TOP_STYLE, CSS.NONE);
		assertEquals(new Integer(0), prop.calculate(MockLU.createFloat(LexicalUnit.SAC_INCH, 0.2f), parentStyles, styles, null));
		styles.put(CSS.BORDER_TOP_STYLE, CSS.HIDDEN);
		assertEquals(new Integer(0), prop.calculate(MockLU.createFloat(LexicalUnit.SAC_INCH, 0.2f), parentStyles, styles, null));

		// check that we use the proper PPI
		styles.put(CSS.BORDER_LEFT_STYLE, CSS.SOLID);
		prop = new BorderWidthProperty(CSS.BORDER_LEFT_WIDTH, CSS.BORDER_LEFT_STYLE, IProperty.Axis.HORIZONTAL);
		assertEquals(Integer.valueOf(10), prop.calculate(MockLU.createFloat(LexicalUnit.SAC_INCH, 0.2f), parentStyles, styles, null));
	}

	@Test
	public void testStringBackgroundImage() throws Exception {
		final Styles styles = new Styles();
		final Styles parentStyles = new Styles();
		final BackgroundImageProperty property = new BackgroundImageProperty();
		assertEquals("http://www.eclipse.org", property.calculate(MockLU.createString("http://www.eclipse.org"), parentStyles, styles, null));
	}

	@Test
	public void testAttrBackgroundImage() throws Exception {
		final LexicalUnit attrSrc = MockLU.createAttr("src");
		final Styles styles = new Styles();
		final Styles parentStyles = new Styles();
		final IDocument document = new DocumentReader().read("<root><image/><image src=\"image.jpg\"/><image src=\"\"/></root>");
		final Iterator<? extends IElement> childElements = document.getRootElement().childElements().iterator();
		final IElement noAttribute = childElements.next();
		final IElement setAttribute = childElements.next();
		final IElement emptyAttribute = childElements.next();
		final BackgroundImageProperty property = new BackgroundImageProperty();

		assertNull(property.calculate(attrSrc, parentStyles, styles, noAttribute));
		assertEquals("image.jpg", property.calculate(attrSrc, parentStyles, styles, setAttribute));
		assertNull(property.calculate(attrSrc, parentStyles, styles, emptyAttribute));
	}

	@Test
	public void testParsePropertyValue() throws Exception {
		final Parser parser = StyleSheetReader.createParser();
		final InputSource source = new InputSource(new StringReader("300px"));
		final LexicalUnit lexicalUnit = parser.parsePropertyValue(source);
		assertEquals(LexicalUnit.SAC_PIXEL, lexicalUnit.getLexicalUnitType());
		assertEquals(300f, lexicalUnit.getFloatValue(), 0f);
	}

	@Test
	public void testOutlineContentProperty() throws Exception {
		final Styles styles = new Styles();
		final IDocument document = new DocumentReader().read("<root><parent><child>child_text</child></parent></root>");

		final IElement parent = document.getRootElement().childElements().first();
		final IElement child = parent.childElements().first();
		final OutlineContentProperty property = new OutlineContentProperty();
		final MockLU lu = MockLU.createIdent("child");
		lu.setNextLexicalUnit(MockLU.createIdent("none"));

		assertEquals(child, property.calculate(lu, styles, null, parent));
		assertEquals(null, property.calculate(lu, styles, null, child));
	}

	@Test
	public void contentProperty_textualContent() throws Exception {
		final ContentProperty property = new ContentProperty();
		final LexicalUnit lu = MockLU.createString("textual content");
		final List<IPropertyContent> propertyContent = property.calculate(lu, null, null, null);
		assertEquals(1, propertyContent.size());
		assertEquals("textual content", propertyContent.get(0).toString());
	}

	@Test
	public void contentProperty_attributeDependendContent() throws Exception {
		final IDocument document = new DocumentReader().read("<root><elem textAttr=\"textual content from attribute\"/></root>");
		final IElement element = document.getRootElement().childElements().first();
		final LexicalUnit lu = MockLU.createAttr("textAttr");
		final List<IPropertyContent> propertyContent = new ContentProperty().calculate(lu, null, null, element);
		assertEquals(1, propertyContent.size());
		final IPropertyContent firstContent = propertyContent.get(0);
		assertEquals("textual content from attribute", firstContent.toString());

		element.setAttribute("textAttr", "changed textual content");
		assertEquals("changed textual content", firstContent.toString());
	}

	@Test
	public void contentProperty_processingInstructionTarget() throws Exception {
		final IDocument document = new DocumentReader().read("<root><?piTarget?></root>");
		final IProcessingInstruction pi = (IProcessingInstruction) document.getRootElement().children().first();
		final LexicalUnit lu = MockLU.createAttr("target");
		final List<IPropertyContent> propertyContent = new ContentProperty().calculate(lu, null, null, pi);
		assertEquals(1, propertyContent.size());
		final IPropertyContent firstContent = propertyContent.get(0);
		assertEquals("piTarget", firstContent.toString());

		pi.setTarget("anotherTarget");
		assertEquals("anotherTarget", firstContent.toString());
	}

	@Test
	public void contentProperty_image_attributeValue() throws Exception {
		final IDocument document = new DocumentReader().read("<root><image src=\"image.jpg\"/></root>");
		final IElement imageElement = document.getRootElement().childElements().first();
		imageElement.setBaseURI("https://www.eclipse.org");
		final LexicalUnit lu = MockLU.createImage(MockLU.createAttr("src"));
		final List<IPropertyContent> propertyContent = new ContentProperty().calculate(lu, null, null, imageElement);
		assertEquals(1, propertyContent.size());
		final IPropertyContent firstContent = propertyContent.get(0);
		assertEquals("https://www.eclipse.org/image.jpg", ((ImageContent) firstContent).getResolvedImageURL().toString());

		imageElement.setAttribute("src", "anotherImage.jpg");
		assertEquals("https://www.eclipse.org/anotherImage.jpg", ((ImageContent) firstContent).getResolvedImageURL().toString());
	}

	private static class DummyDisplayDevice extends DisplayDevice {
		public DummyDisplayDevice(final int horizontalPPI, final int verticalPPI) {
			this.horizontalPPI = horizontalPPI;
			this.verticalPPI = verticalPPI;
		}

		@Override
		public int getHorizontalPPI() {
			return horizontalPPI;
		}

		@Override
		public int getVerticalPPI() {
			return verticalPPI;
		}

		private final int horizontalPPI;
		private final int verticalPPI;
	}
}
