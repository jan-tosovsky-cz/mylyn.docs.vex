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
package org.eclipse.vex.ui.internal.handlers;

import java.util.NoSuchElementException;

import org.eclipse.vex.core.internal.widget.IDocumentEditor;
import org.eclipse.vex.core.provisional.dom.ContentPosition;
import org.eclipse.vex.core.provisional.dom.IElement;

/**
 * Navigates to the previous table cell (usual shortcut: {@code Shift+Tab}).
 *
 * @see NextTableCellHandler
 */
public class PreviousTableCellHandler extends AbstractNavigateTableCellHandler {

	@Override
	protected void navigate(final IDocumentEditor editor, final IElement tableRow, final ContentPosition position) {
		IElement siblingCell = null;
		for (final IElement cell : tableRow.childElements()) {
			if (cell.getEndPosition().isAfterOrEquals(position)) {
				break;
			}
			siblingCell = cell;
		}

		// in this row
		if (siblingCell != null) {
			editor.moveTo(siblingCell.getStartPosition().moveBy(1));
			return;
		}

		IElement siblingRow = null;
		for (final IElement row : tableRow.getParentElement().childElements()) {
			if (row.getEndPosition().isAfterOrEquals(position)) {
				break;
			}
			siblingRow = row;
		}

		// in other row
		if (siblingRow != null) {
			final IElement lastCell = lastCellOf(siblingRow);
			if (lastCell != null) {
				editor.moveTo(lastCell.getStartPosition().moveBy(1));
			}
		}
	}

	private static IElement lastCellOf(final IElement tableRow) {
		try {
			return tableRow.childElements().last();
		} catch (final NoSuchElementException e) {
			return null;
		}
	}
}
