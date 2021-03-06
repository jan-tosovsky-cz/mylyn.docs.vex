/*******************************************************************************
 * Copyright (c) 2004, 2008 John Krasnay and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Krasnay - initial API and implementation
 *     Igor Jacy Lino Campista - Java 5 warnings fixed (bug 311325)
 *******************************************************************************/
package org.eclipse.vex.ui.internal.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.vex.core.internal.dom.CopyOfElement;
import org.eclipse.vex.core.internal.layout.LayoutUtils.ElementOrRange;
import org.eclipse.vex.core.internal.widget.IDocumentEditor;
import org.eclipse.vex.core.provisional.dom.ContentPosition;
import org.eclipse.vex.core.provisional.dom.IElement;

/**
 * Inserts a single table column before (left of) or after (right of) the current one.
 *
 * @see AddColumnLeftHandler
 * @see AddColumnRightHandler
 */
public abstract class AbstractAddColumnHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IDocumentEditor editor = VexHandlerUtil.getDocumentEditor(event);
		editor.doWork(new Runnable() {
			@Override
			public void run() {
				try {
					addColumn(editor);
				} catch (final ExecutionException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return null;
	}

	private void addColumn(final IDocumentEditor editor) throws ExecutionException {
		final int indexToDup = VexHandlerUtil.getCurrentColumnIndex(editor);

		// adding possible?
		if (indexToDup == -1) {
			return;
		}

		final List<IElement> cellsToDup = new ArrayList<IElement>();
		VexHandlerUtil.iterateTableCells(editor, new TableCellCallbackAdapter() {
			@Override
			public void onCell(final ElementOrRange row, final ElementOrRange cell, final int rowIndex, final int cellIndex) {
				if (cellIndex == indexToDup && cell instanceof IElement) {
					cellsToDup.add((IElement) cell);
				}
			}
		});

		for (final IElement element : cellsToDup) {
			editor.moveTo(addBefore() ? element.getStartPosition() : element.getEndPosition().moveBy(1));
			editor.insertElement(element.getQualifiedName()).accept(new CopyOfElement(element));
		}

		// Place caret in first new cell
		final IElement firstCell = cellsToDup.get(0);
		final ContentPosition position = new ContentPosition(firstCell, firstCell.getStartOffset() + 1);
		editor.moveTo(position);
	}

	/**
	 * @return {@code true} to add new column before (left of) current column or {@code false} to add new column after
	 *         (right of) current column
	 */
	protected abstract boolean addBefore();

}
