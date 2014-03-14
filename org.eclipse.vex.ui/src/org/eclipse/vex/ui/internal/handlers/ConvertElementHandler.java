/*******************************************************************************
 * Copyright (c) 2004, 2014 John Krasnay and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Krasnay - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.ui.internal.handlers;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.vex.core.internal.widget.swt.VexWidget;
import org.eclipse.vex.ui.internal.swt.ContentAssist;

/**
 * Shows the content assist to convert current element ({@link MorphAssistant}).
 */
public class ConvertElementHandler extends AbstractVexWidgetHandler implements IElementUpdater {

	/** ID of the corresponding convert element command. */
	public static final String COMMAND_ID = "org.eclipse.vex.ui.ConvertElementCommand"; //$NON-NLS-1$

	private static final String LABEL_ID = "command.convertElement.dynamicName"; //$NON-NLS-1$

	@Override
	public void execute(final VexWidget widget) throws ExecutionException {
		ContentAssist.openQuickFixContentAssist(widget);
	}

	public void updateElement(final UIElement element, @SuppressWarnings("rawtypes") final Map parameters) {
		updateElement(element, parameters, LABEL_ID, LABEL_ID);
	}

}
