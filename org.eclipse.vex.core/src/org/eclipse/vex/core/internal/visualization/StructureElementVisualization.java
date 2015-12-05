/*********************************************import static org.eclipse.vex.core.internal.boxes.BoxFactory.frame;
import static org.eclipse.vex.core.internal.boxes.BoxFactory.nodeReference;
import static org.eclipse.vex.core.internal.boxes.BoxFactory.verticalBlock;

import org.eclipse.vex.core.provisional.dom.IElement;
ailable at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.core.internal.visualization;

import static org.eclipse.vex.core.internal.boxes.BoxFactory.frame;
import static org.eclipse.vex.core.internal.boxes.BoxFactory.nodeReference;
import static org.eclipse.vex.core.internal.boxes.BoxFactory.verticalBlock;

import org.eclipse.vex.core.internal.boxes.Border;
import org.eclipse.vex.core.internal.boxes.IStructuralBox;
import org.eclipse.vex.core.internal.boxes.Margin;
import org.eclipse.vex.core.internal.boxes.Padding;
import org.eclipse.vex.core.provisional.dom.IElement;

public final class StructureElementVisualization extends NodeVisualization<IStructuralBox> {
	@Override
	public IStructuralBox visit(final IElement element) {
		return nodeReference(element, frame(visualizeChildrenStructure(element.children(), verticalBlock()), Margin.NULL, Border.NULL, new Padding(3, 3)));
	}
}
