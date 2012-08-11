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

import org.eclipse.vex.core.internal.core.ColorResource;
import org.eclipse.vex.core.internal.core.FontResource;
import org.eclipse.vex.core.internal.core.Graphics;
import org.eclipse.vex.core.internal.css.Styles;
import org.eclipse.vex.core.internal.dom.Document;
import org.eclipse.vex.core.internal.dom.Element;
import org.eclipse.vex.core.internal.dom.Text;

/**
 * A TextBox that gets its text from the document. Represents text which is editable within the VexWidget.
 */
public class DocumentTextBox extends TextBox {

	private final int startRelative;
	private final int endRelative;

	/**
	 * Class constructor, accepting a Text object.
	 * 
	 * @param context
	 *            LayoutContext in use
	 * @param element
	 *            Element being used
	 * @param text
	 */
	public DocumentTextBox(final LayoutContext context, final Element element, final Text text) {
		this(context, element, text.getStartOffset(), text.getEndOffset());
	}

	/**
	 * Class constructor.
	 * 
	 * @param context
	 *            LayoutContext used to calculate the box's size.
	 * @param element
	 *            Element that directly contains the text.
	 * @param startOffset
	 *            start offset of the text
	 * @param endOffset
	 *            end offset of the text
	 */
	public DocumentTextBox(final LayoutContext context, final Element element, final int startOffset, final int endOffset) {
		super(element);

		if (startOffset >= endOffset) {
			throw new IllegalStateException("DocumentTextBox: startOffset (" + startOffset + ") >= endOffset (" + endOffset + ")");
		}

		startRelative = startOffset - element.getStartOffset();
		endRelative = endOffset - element.getStartOffset();
		calculateSize(context);

		if (getText().length() < endOffset - startOffset) {
			throw new IllegalStateException();
		}
	}

	/**
	 * @see org.eclipse.vex.core.internal.layout.Box#getEndOffset()
	 */
	@Override
	public int getEndOffset() {
		if (endRelative == -1) {
			return -1;
		} else {
			return getElement().getStartOffset() + endRelative - 1;
		}
	}

	/**
	 * @see org.eclipse.vex.core.internal.layout.Box#getStartOffset()
	 */
	@Override
	public int getStartOffset() {
		if (startRelative == -1) {
			return -1;
		} else {
			return getElement().getStartOffset() + startRelative;
		}
	}

	/**
	 * @see org.eclipse.vex.core.internal.layout.TextBox#getText()
	 */
	@Override
	public String getText() {
		final Document doc = getElement().getDocument();
		return doc.getText(getStartOffset(), getEndOffset() + 1);
	}

	/**
	 * @see org.eclipse.vex.core.internal.layout.Box#hasContent()
	 */
	@Override
	public boolean hasContent() {
		return true;
	}

	/**
	 * @see org.eclipse.vex.core.internal.layout.Box#paint(org.eclipse.vex.core.internal.layout.LayoutContext, int, int)
	 */
	@Override
	public void paint(final LayoutContext context, final int x, final int y) {

		final Styles styles = context.getStyleSheet().getStyles(getElement());
		final Graphics g = context.getGraphics();

		final FontResource font = g.createFont(styles.getFont());
		final FontResource oldFont = g.setFont(font);
		final ColorResource foreground = g.createColor(styles.getColor());
		final ColorResource oldForeground = g.setColor(foreground);
		// ColorResource background =
		// g.createColor(styles.getBackgroundColor());
		// ColorResource oldBackground = g.setBackgroundColor(background);

		final char[] chars = getText().toCharArray();

		if (chars.length < getEndOffset() - getStartOffset()) {
			throw new IllegalStateException();
		}

		if (chars.length == 0) {
			throw new IllegalStateException();
		}

		final int start = 0;
		int end = chars.length;
		if (chars[end - 1] == NEWLINE_CHAR) {
			end--;
		}
		int selStart = context.getSelectionStart() - getStartOffset();
		selStart = Math.min(Math.max(selStart, start), end);
		int selEnd = context.getSelectionEnd() - getStartOffset();
		selEnd = Math.min(Math.max(selEnd, start), end);

		// text before selection
		if (start < selStart) {
			g.drawChars(chars, start, selStart - start, x, y);
			final String s = new String(chars, start, selStart - start);
			paintTextDecoration(context, styles, s, x, y);
		}

		// text after selection
		if (selEnd < end) {
			final int x1 = x + g.charsWidth(chars, 0, selEnd);
			g.drawChars(chars, selEnd, end - selEnd, x1, y);
			final String s = new String(chars, selEnd, end - selEnd);
			paintTextDecoration(context, styles, s, x1, y);
		}

		// text within selection
		if (selStart < selEnd) {
			final String s = new String(chars, selStart, selEnd - selStart);
			final int x1 = x + g.charsWidth(chars, 0, selStart);
			paintSelectedText(context, s, x1, y);
			paintTextDecoration(context, styles, s, x1, y);
		}

		g.setFont(oldFont);
		g.setColor(oldForeground);
		// g.setBackgroundColor(oldBackground);
		font.dispose();
		foreground.dispose();
		// background.dispose();
	}

	/**
	 * @see org.eclipse.vex.core.internal.layout.TextBox#splitAt(int)
	 */
	@Override
	public Pair splitAt(final LayoutContext context, final int offset) {

		if (offset < 0 || offset > endRelative - startRelative) {
			throw new IllegalStateException();
		}

		final int split = getStartOffset() + offset;

		DocumentTextBox left;
		if (offset == 0) {
			left = null;
		} else {
			left = new DocumentTextBox(context, getElement(), getStartOffset(), split);
		}

		InlineBox right;
		if (split == getEndOffset() + 1) {
			right = null;
		} else {
			right = new DocumentTextBox(context, getElement(), split, getEndOffset() + 1);
		}
		return new Pair(left, right);
	}

	/**
	 * @see org.eclipse.vex.core.internal.layout.Box#viewToModel(org.eclipse.vex.core.internal.layout.LayoutContext,
	 *      int, int)
	 */
	@Override
	public int viewToModel(final LayoutContext context, final int x, final int y) {

		final Graphics g = context.getGraphics();
		final Styles styles = context.getStyleSheet().getStyles(getElement());
		final FontResource font = g.createFont(styles.getFont());
		final FontResource oldFont = g.setFont(font);
		final char[] chars = getText().toCharArray();

		if (getWidth() <= 0) {
			return getStartOffset();
		}

		// first, get an estimate based on x / width
		int offset = x / getWidth() * chars.length;
		offset = Math.max(0, offset);
		offset = Math.min(chars.length, offset);

		int delta = Math.abs(x - g.charsWidth(chars, 0, offset));

		// Search backwards
		while (offset > 0) {
			final int newDelta = Math.abs(x - g.charsWidth(chars, 0, offset - 1));
			if (newDelta > delta) {
				break;
			}
			delta = newDelta;
			offset--;
		}

		// Search forwards
		while (offset < chars.length - 1) {
			final int newDelta = Math.abs(x - g.charsWidth(chars, 0, offset + 1));
			if (newDelta > delta) {
				break;
			}
			delta = newDelta;
			offset++;
		}

		g.setFont(oldFont);
		font.dispose();
		return getStartOffset() + offset;
	}

}
