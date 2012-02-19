/*******************************************************************************
 * Copyright (c) 2011 Florian Thienel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.core.internal.dom;

import org.eclipse.core.runtime.QualifiedName;

/**
 * Represents a comment
 */
public class CommentElement extends Element {

	public static final QualifiedName COMMENT_ELEMENT_NAME = new QualifiedName(null, "!COMMENT");
	
	public CommentElement() {
		super(COMMENT_ELEMENT_NAME);
	}

}
