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

/**
 * @author Florian Thienel
 */
public interface IBoxVisitor {

	void visit(RootBox box);

	void visit(VerticalBlock box);

	void visit(StructuralFrame box);

	void visit(StructuralNodeReference box);

	void visit(HorizontalBar box);

	void visit(List box);

	void visit(ListItem box);

	void visit(Table box);

	void visit(TableRowGroup box);

	void visit(TableColumnSpec box);

	void visit(TableRow box);

	void visit(TableCell box);

	void visit(Paragraph box);

	void visit(InlineNodeReference box);

	void visit(InlineContainer box);

	void visit(InlineFrame box);

	void visit(StaticText box);

	void visit(Image box);

	void visit(TextContent box);

	void visit(NodeEndOffsetPlaceholder box);

	void visit(GraphicalBullet box);

	void visit(Square box);

	void visit(NodeTag box);
}
