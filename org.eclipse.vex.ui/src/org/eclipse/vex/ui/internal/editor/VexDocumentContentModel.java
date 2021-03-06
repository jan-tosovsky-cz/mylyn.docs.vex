/*******************************************************************************
 * Copyright (c) 2011, 2013 Florian Thienel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.ui.internal.editor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.vex.core.provisional.dom.DocumentContentModel;
import org.eclipse.vex.core.provisional.dom.IElement;
import org.eclipse.vex.ui.internal.VexPlugin;
import org.eclipse.vex.ui.internal.config.DocumentType;
import org.eclipse.vex.ui.internal.config.Style;

/**
 * @author Florian Thienel
 */
public class VexDocumentContentModel extends DocumentContentModel {

	private final Shell shell;

	private DocumentType documentType;

	private Style style;

	private boolean shouldAssignInferredDocumentType;

	public VexDocumentContentModel(final Shell shell) {
		this.shell = shell;
	}

	/**
	 * This constructor is only menat to be used for unit testing.
	 */
	public VexDocumentContentModel() {
		shell = null;
	}

	@Override
	public void initialize(final String baseUri, final String publicId, final String systemId, final IElement rootElement) {
		super.initialize(baseUri, publicId, systemId, rootElement);
		final String mainDocumentTypeIdentifier = getMainDocumentTypeIdentifier();
		documentType = getRegisteredDocumentType();
		if (documentType == null && shell != null) {
			documentType = queryUserForDocumentType();
			if (documentType == null) {
				throw new NoRegisteredDoctypeException(mainDocumentTypeIdentifier, false);
			}
			// Reinitialize after the user selected a doctype
			if (documentType.getNamespaceName() != null) {
				setSchemaId(baseUri, documentType.getNamespaceName());
			} else {
				super.initialize(baseUri, documentType.getPublicId(), documentType.getSystemId(), null);
			}
		}

		// TODO verify documentType URL???
		//		final URL url = documentType.getResourceUrl();
		//		if (url == null) {
		//			final String message = MessageFormat.format(Messages.getString("VexEditor.noUrlForDoctype"), mainDocumentTypeIdentifier);
		//			throw new RuntimeException(message);
		//		}
		style = VexPlugin.getDefault().getPreferences().getPreferredStyle(getDocumentType());
		if (style == null) {
			throw new NoStyleForDoctypeException();
		}
	}

	private DocumentType getRegisteredDocumentType() {
		if (getMainDocumentTypeIdentifier() == null) {
			return null;
		}
		final DocumentType doctype = VexPlugin.getDefault().getConfigurationRegistry().getDocumentType(getMainDocumentTypeIdentifier(), getSystemId());
		if (doctype != null) {
			return doctype;
		}
		return null;
	}

	private DocumentType queryUserForDocumentType() {
		final DocumentTypeSelectionDialog dialog = DocumentTypeSelectionDialog.create(shell, getMainDocumentTypeIdentifier());
		if (dialog.open() == Window.CANCEL) {
			throw new NoRegisteredDoctypeException(null, true);
		}
		if (dialog.alwaysUseThisDoctype()) {
			shouldAssignInferredDocumentType = true;
		}
		return dialog.getDoctype();
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public Style getStyle() {
		return style;
	}

	public boolean shouldAssignInferredDocumentType() {
		return shouldAssignInferredDocumentType;
	}
}
