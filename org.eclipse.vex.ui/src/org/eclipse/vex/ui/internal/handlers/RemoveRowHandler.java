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

import java.util.List;

import org.eclipse.vex.core.internal.widget.IDocumentEditor;

/**
 * Deletes selected row(s).
 */
public class RemoveRowHandler extends AbstractRemoveTableCellsHandler {

	@Override
	protected List<Object> collectCellsToDelete(final IDocumentEditor editor, final VexHandlerUtil.RowColumnInfo rcInfo) {
		return VexHandlerUtil.getSelectedTableRows(editor).getRows();
	}

}
