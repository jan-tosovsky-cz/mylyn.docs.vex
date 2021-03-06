/*******************************************************************************
 * Copyright (c) 2014 Florian Thienel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.core.internal.boxes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import org.eclipse.vex.core.internal.core.Graphics;
import org.eclipse.vex.core.internal.core.TextAlign;

public class LineArrangement {

	private final LinkedList<Line> lines = new LinkedList<Line>();

	private ListIterator<IInlineBox> boxIterator;
	private int width;
	private int height;
	private boolean lastBoxWrappedCompletely;
	private Line currentLine;

	public void arrangeBoxes(final Graphics graphics, final ListIterator<IInlineBox> boxIterator, final int width, final TextAlign textAlign) {
		this.boxIterator = boxIterator;
		this.width = width;
		reset();

		while (boxIterator.hasNext()) {
			final IInlineBox box = boxIterator.next();
			box.setMaxWidth(width);
			box.layout(graphics);
			appendBox(graphics, box);
		}
		finalizeCurrentLine();

		alignLines(graphics, textAlign);
	}

	private void reset() {
		lines.clear();
		height = 0;
		lastBoxWrappedCompletely = false;
		currentLine = new Line();
	}

	private void appendBox(final Graphics graphics, final IInlineBox box) {
		final boolean boxWrappedCompletely;
		if (currentLine.canJoinWithLastChild(box)) {
			boxWrappedCompletely = arrangeWithLastChild(graphics, box);
		} else if ((boxFitsIntoCurrentLine(box) || !hasVisibleContent(box)) && !box.requiresSplitForLineWrapping()) {
			boxWrappedCompletely = appendToCurrentLine(box);
		} else if (box.canSplit()) {
			boxWrappedCompletely = splitAndWrapToNextLine(graphics, box);
		} else {
			boxWrappedCompletely = wrapCompletelyToNextLine(box);
		}

		lastBoxWrappedCompletely = boxWrappedCompletely;
	}

	private static boolean hasVisibleContent(final IInlineBox box) {
		final Boolean result = box.accept(new DepthFirstBoxTraversal<Boolean>() {
			@Override
			public Boolean visit(final TextContent box) {
				return box.getText().trim().length() > 0;
			}

			@Override
			public Boolean visit(final StaticText box) {
				return box.getText().trim().length() > 0;
			}
		});
		if (result == null) {
			return true;
		}
		return result;
	}

	private boolean arrangeWithLastChild(final Graphics graphics, final IInlineBox box) {
		currentLine.joinWithLastChild(box);
		boxIterator.remove();
		if (currentLine.getWidth() <= width) {
			return false;
		}

		final IInlineBox lastChild = currentLine.getLastChild();
		if (!lastChild.canSplit()) {
			throw new IllegalStateException("An IInlineBox that supports joining must also support splitting!");
		}

		final int headWidth = lastChild.getWidth() - currentLine.getWidth() + width;
		final IInlineBox tail = lastChild.splitTail(graphics, headWidth, !currentLine.hasMoreThanOneChild());
		final boolean lastChildWrappedCompletely = lastChild.getWidth() == 0;
		if (lastChildWrappedCompletely) {
			removeLastChild();
		}
		if (tail.getWidth() > 0) {
			insertNextBox(tail);
			lineBreak();
		}

		return lastChildWrappedCompletely;
	}

	private void removeLastChild() {
		boxIterator.previous();
		boxIterator.remove();
		currentLine.removeLastChild();
	}

	private boolean appendToCurrentLine(final IInlineBox box) {
		currentLine.appendChild(box);
		return false;
	}

	private boolean splitAndWrapToNextLine(final Graphics graphics, final IInlineBox box) {
		if (box.getLineWrappingAtStart() == LineWrappingRule.REQUIRED) {
			return wrapCompletelyToNextLine(box);
		}

		final int headWidth = width - currentLine.getWidth();
		final IInlineBox tail = box.splitTail(graphics, headWidth, !currentLine.hasChildren());
		final boolean boxWrappedCompletely = box.getWidth() == 0;
		if (boxWrappedCompletely) {
			boxIterator.remove();
		} else {
			currentLine.appendChild(box);
		}

		if (tail.getWidth() > 0) {
			insertNextBox(tail);
			lineBreak();
		} else if (box.getLineWrappingAtEnd() == LineWrappingRule.REQUIRED) {
			lineBreak();
		}

		return boxWrappedCompletely;
	}

	private boolean wrapCompletelyToNextLine(final IInlineBox box) {
		lineBreak();
		if (boxFitsIntoCurrentLine(box)) {
			currentLine.appendChild(box);
		}
		return true;
	}

	private void insertNextBox(final IInlineBox box) {
		boxIterator.add(box);
		backupBoxIterator();
	}

	private void backupBoxIterator() {
		if (!lastBoxWrappedCompletely) {
			boxIterator.previous();
		}
	}

	private boolean boxFitsIntoCurrentLine(final IInlineBox box) {
		return currentLine.getWidth() + box.getWidth() <= width;
	}

	private void lineBreak() {
		finalizeCurrentLine();
		currentLine = new Line();
	}

	private void finalizeCurrentLine() {
		if (currentLine.getWidth() <= 0) {
			return;
		}
		currentLine.arrangeChildren();
		currentLine.setPosition(height, 0);
		height += currentLine.getHeight();
		lines.add(currentLine);
	}

	private void alignLines(final Graphics graphics, final TextAlign textAlign) {
		if (textAlign == TextAlign.LEFT) {
			return;
		}
		for (final Line line : lines) {
			line.shiftBy(alignmentOffset(graphics, line, textAlign));
		}
	}

	private int alignmentOffset(final Graphics graphics, final Line line, final TextAlign textAlign) {
		switch (textAlign) {
		case CENTER:
			return (width - line.getWidth() + line.getInvisibleGapLeft(graphics) + line.getInvisibleGapRight(graphics)) / 2 - line.getInvisibleGapLeft(graphics);
		case RIGHT:
			return width - line.getWidth() + line.getInvisibleGapRight(graphics);
		default:
			return 0;
		}
	}

	public Collection<Line> getLines() {
		return lines;
	}

	public int getHeight() {
		return height;
	}
}
