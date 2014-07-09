/*******************************************************************************
 * Copyright (c) 2004, 2010 John Krasnay and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Krasnay - initial API and implementation
 *     Florian Thienel - bug 304413 - fix immutable array issue.
 *     Igor Jacy Lino Campista - Java 5 warnings fixed (bug 311325)
 *******************************************************************************/
package org.eclipse.vex.core.internal.css;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.vex.core.provisional.dom.INode;
import org.w3c.css.sac.LexicalUnit;

/**
 * The CSS 'font-family' property.
 */
public class FontFamilyProperty extends AbstractProperty {

	/**
	 * Class constructor.
	 */
	public FontFamilyProperty() {
		super(CSS.FONT_FAMILY);
	}

	@Override
	public Object calculate(final LexicalUnit lu, final Styles parentStyles, final Styles styles, final INode node) {
		if (isFontFamily(lu)) {
			return getFontFamilies(lu);
		} else {
			// not specified, "inherit", or some other value
			if (parentStyles != null) {
				return parentStyles.getFontFamilies();
			} else {
				final String[] fonts = new String[DEFAULT_FONT_FAMILY.length];
				System.arraycopy(DEFAULT_FONT_FAMILY, 0, fonts, 0, DEFAULT_FONT_FAMILY.length);
				return fonts;
			}
		}
	}

	private static final String[] DEFAULT_FONT_FAMILY = new String[] { "sans-serif" };

	private static boolean isFontFamily(final LexicalUnit lu) {
		return lu != null && (lu.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE || lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT);
	}

	private static String[] getFontFamilies(LexicalUnit lu) {
		final List<String> list = new ArrayList<String>();
		while (lu != null) {
			if (lu.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE || lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {

				list.add(lu.getStringValue());
			}
			lu = lu.getNextLexicalUnit();
		}
		return list.toArray(new String[list.size()]);
	}

}
