/*******************************************************************************
 * Copyright (c) 2012, 2014 Florian Thienel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 * 		Carsten Hiesserich - added processing instruction and include
 *******************************************************************************/
package org.eclipse.vex.core.provisional.dom;

/**
 * This class provides default implementations for the methods defined by the <code>INodeVisitor</code> interface.
 *
 * @see INodeVisitor
 * @author Florian Thienel
 */
public class BaseNodeVisitor implements INodeVisitor {

	@Override
	public void visit(final IDocument document) {
		// ignore
	}

	@Override
	public void visit(final IDocumentFragment fragment) {
		// ignore
	}

	@Override
	public void visit(final IElement element) {
		// ignore
	}

	@Override
	public void visit(final IText text) {
		// ignore
	}

	@Override
	public void visit(final IComment comment) {
		// ignore
	}

	@Override
	public void visit(final IProcessingInstruction pi) {
		// ignore
	}

	@Override
	public void visit(final IIncludeNode include) {
		// ignore
	}
}
