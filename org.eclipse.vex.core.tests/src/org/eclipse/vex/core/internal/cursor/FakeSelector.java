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
package org.eclipse.vex.core.internal.cursor;

import org.eclipse.vex.core.provisional.dom.ContentRange;

/**
 * @author Florian Thienel
 */
public class FakeSelector implements IContentSelector {

	@Override
	public void setMark(final int offset) {
	}

	@Override
	public void moveEndTo(final int offset) {
	}

	@Override
	public void setEndAbsoluteTo(final int offset) {
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public int getStartOffset() {
		return 0;
	}

	@Override
	public int getEndOffset() {
		return 0;
	}

	@Override
	public ContentRange getRange() {
		return ContentRange.NULL;
	}

	@Override
	public int getCaretOffset() {
		return 0;
	}

}
