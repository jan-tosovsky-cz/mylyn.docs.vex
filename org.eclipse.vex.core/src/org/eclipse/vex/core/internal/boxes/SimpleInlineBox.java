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

import org.eclipse.vex.core.internal.core.Graphics;
import org.eclipse.vex.core.internal.core.Rectangle;

/**
 * @author Florian Thienel
 */
public abstract class SimpleInlineBox extends BaseBox implements IInlineBox {

	private IBox parent;
	private int top;
	private int left;
	private int maxWidth;

	private LineWrappingRule lineWrappingAtStart = LineWrappingRule.ALLOWED;
	private LineWrappingRule lineWrappingAtEnd = LineWrappingRule.ALLOWED;

	@Override
	public void setParent(final IBox parent) {
		this.parent = parent;
	}

	@Override
	public IBox getParent() {
		return parent;
	}

	@Override
	public int getAbsoluteTop() {
		if (parent == null) {
			return top;
		}
		return parent.getAbsoluteTop() + top;
	}

	@Override
	public int getAbsoluteLeft() {
		if (parent == null) {
			return left;
		}
		return parent.getAbsoluteLeft() + left;
	}

	@Override
	public int getTop() {
		return top;
	}

	@Override
	public int getLeft() {
		return left;
	}

	@Override
	public void setPosition(final int top, final int left) {
		this.top = top;
		this.left = left;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(left, top, getWidth(), getHeight());
	}

	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	@Override
	public void setMaxWidth(final int maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public int getInvisibleGapAtStart(final Graphics graphics) {
		return 0;
	}

	@Override
	public int getInvisibleGapAtEnd(final Graphics graphics) {
		return 0;
	}

	@Override
	public LineWrappingRule getLineWrappingAtStart() {
		return lineWrappingAtStart;
	}

	public void setLineWrappingAtStart(final LineWrappingRule wrappingRule) {
		lineWrappingAtStart = wrappingRule;
	}

	@Override
	public LineWrappingRule getLineWrappingAtEnd() {
		return lineWrappingAtEnd;
	}

	public void setLineWrappingAtEnd(final LineWrappingRule wrappingRule) {
		lineWrappingAtEnd = wrappingRule;
	}

	@Override
	public boolean requiresSplitForLineWrapping() {
		return lineWrappingAtStart == LineWrappingRule.REQUIRED || lineWrappingAtEnd == LineWrappingRule.REQUIRED;
	}

	@Override
	public boolean canJoin(final IInlineBox other) {
		return false;
	}

	@Override
	public boolean join(final IInlineBox other) {
		return false;
	}

	@Override
	public boolean canSplit() {
		return false;
	}

	@Override
	public IInlineBox splitTail(final Graphics graphics, final int headWidth, final boolean force) {
		throw new UnsupportedOperationException("Splitting is not supported for " + getClass().getName() + ".");
	}
}
