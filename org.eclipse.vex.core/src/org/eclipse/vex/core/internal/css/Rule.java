/*******************************************************************************
 * Copyright (c) 2004, 2013 John Krasnay and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Krasnay - initial API and implementation
 *     Dave Holroyd - Proper specificity for wildcard selector
 *     John Austin - Implement sibling selectors
 *     Florian Thienel - bug 306639 - remove serializability from StyleSheet
 *                       and dependend classes
 *     Carsten Hiesserich - changed pseudo element handling
 *******************************************************************************/
package org.eclipse.vex.core.internal.css;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.vex.core.internal.dom.Namespace;
import org.eclipse.vex.core.provisional.dom.BaseNodeVisitorWithResult;
import org.eclipse.vex.core.provisional.dom.IComment;
import org.eclipse.vex.core.provisional.dom.IDocument;
import org.eclipse.vex.core.provisional.dom.IElement;
import org.eclipse.vex.core.provisional.dom.INode;
import org.eclipse.vex.core.provisional.dom.IParent;
import org.eclipse.vex.core.provisional.dom.IProcessingInstruction;
import org.w3c.css.sac.AttributeCondition;
import org.w3c.css.sac.CombinatorCondition;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.NegativeSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SiblingSelector;

/**
 * Represents a pairing of a selector with a list of styles. This does not exactly correspond to a rule in a style
 * sheet; there is only one selector associated with an instance of this class, whereas multiple selectors may be
 * associated with a style sheet rule.
 *
 * Note: <code>Rule</code> implements the <code>Comparable</code> interface in order to be sorted by "specificity" as
 * defined by the CSS spec. However, this ordering is <em>not</em> consistent with <code>equals</code> (rules with the
 * same specificity may not be equal). Therefore, <code>Rule</code> objects should not be used with sorted collections
 * or maps in the <code>java.util</code> package, unless a suitable <code>Comparator</code> is also used.
 */
public class Rule {

	public static final String COMMENT_RULE_NAME = "COMMENT";

	private final Selector selector;
	private final List<PropertyDecl> propertyDecls = new ArrayList<PropertyDecl>();

	/**
	 * Class constructor.
	 *
	 * @param selector
	 *            Selector for the rule.
	 */
	public Rule(final Selector selector) {
		this.selector = selector;
	}

	/**
	 * Adds a property declaration to the rule.
	 *
	 * @param decl
	 *            new property declaration to add
	 */
	public void add(final PropertyDecl decl) {
		propertyDecls.add(decl);
	}

	/**
	 * Returns the selector for the rule.
	 */
	public Selector getSelector() {
		return selector;
	}

	/**
	 * Returns an array of the property declarations in this rule.
	 */
	public PropertyDecl[] getPropertyDecls() {
		return propertyDecls.toArray(new PropertyDecl[propertyDecls.size()]);
	}

	/**
	 * Calculates the specificity for the selector associated with this rule. The specificity is represented as an
	 * integer whose base-10 representation, xxxyyyzzz, can be decomposed into the number of "id" selectors (xxx),
	 * "class" selectors (yyy), and "element" selectors (zzz). Composite selectors result in a recursive call.
	 */
	public int getSpecificity() {
		return specificity(getSelector());
	}

	/**
	 * Returns true if the given element matches this rule's selector.
	 *
	 * @param node
	 *            Node to check.
	 */
	public boolean matches(final INode node) {
		return matches(selector, node);
	}

	// ==================================================== PRIVATE

	/**
	 * Returns true if the given element matches the given selector.
	 */
	private static boolean matches(final Selector selector, final INode node) {

		if (node == null) {
			// This can happen when, e.g., with the rule "foo > *".
			// Since the root element matches the "*", we check if
			// its parent matches "foo", but of course its parent
			// is null
			return false;
		}

		final int selectorType = selector.getSelectorType();

		switch (selectorType) {
		case Selector.SAC_CONDITIONAL_SELECTOR:
			// We end here for PseudoClass selectors in difference to PseudoElementSelectors.
			// See StyleSheetReader#createParser for defined PseudoElements.
			// according to http://www.w3.org/TR/CSS2/selector.html#pseudo-class names are case insensitive
			final ConditionalSelector cs = (ConditionalSelector) selector;
			if (cs.getCondition().getConditionType() == Condition.SAC_PSEUDO_CLASS_CONDITION) {
				final AttributeCondition ac = (AttributeCondition) cs.getCondition();
				if (node instanceof IComment) {
					return COMMENT_RULE_NAME.equalsIgnoreCase(ac.getValue()) && matches(cs.getSimpleSelector(), node.getParent());
				} else {
					return false;
				}
			} else {
				return matches(cs.getSimpleSelector(), node) && matchesCondition(cs.getCondition(), node);
			}

		case Selector.SAC_ANY_NODE_SELECTOR:
			// You'd think we land here if we have a * rule, but instead
			// it appears we get a SAC_ELEMENT_NODE_SELECTOR with
			// localName==null
			return true;

		case Selector.SAC_ROOT_NODE_SELECTOR:
			return node.getParent() instanceof IDocument;

		case Selector.SAC_NEGATIVE_SELECTOR:
			break; // not yet supported

		case Selector.SAC_ELEMENT_NODE_SELECTOR:
			final String elementName = getLocalNameOfElement(node);
			final String selectorName = ((ElementSelector) selector).getLocalName();

			// If the Selector has a namespace URI, it has to match the namespace URI of the node
			final String selectorNamespaceURI = ((ElementSelector) selector).getNamespaceURI();
			if (selectorNamespaceURI != null && !selectorNamespaceURI.equals(getNamespaceURIOfElement(node))) {
				return false;
			}

			if (selectorName == null) {
				// We land here if we have a wildcard selector (*) or
				// a pseudocondition w/o an element name (:before)
				// Probably other situations too (conditional w/o element
				// name? e.g. [attr=value])
				return true;
			}
			if (selectorName.equals(elementName)) {
				return true;
			}
			return false;

		case Selector.SAC_TEXT_NODE_SELECTOR:
			return false; // not yet supported

		case Selector.SAC_CDATA_SECTION_NODE_SELECTOR:
			return false; // not yet supported

		case Selector.SAC_PROCESSING_INSTRUCTION_NODE_SELECTOR:
			return false; // not yet supported

		case Selector.SAC_COMMENT_NODE_SELECTOR:
			return false; // not yet supported

		case Selector.SAC_PSEUDO_ELEMENT_SELECTOR:
			// Always return true here. The Selector is evaluated with SAC_CHILD_SELECTOR.
			// The pseudo element rules are stored with the parent's rules (see StyleSheet#getApplicableDeclarations)
			return true;

		case Selector.SAC_DESCENDANT_SELECTOR:
			final DescendantSelector ds = (DescendantSelector) selector;
			return matches(ds.getSimpleSelector(), node) && matchesAncestor(ds.getAncestorSelector(), node);

		case Selector.SAC_CHILD_SELECTOR:
			final DescendantSelector ds2 = (DescendantSelector) selector;
			if (ds2.getSimpleSelector().getSelectorType() == Selector.SAC_PSEUDO_ELEMENT_SELECTOR) {
				return matches(ds2.getAncestorSelector(), node);
			}
			final IParent parent = node.getParent();
			return matches(ds2.getSimpleSelector(), node) && matches(ds2.getAncestorSelector(), parent);

		case Selector.SAC_DIRECT_ADJACENT_SELECTOR:

			final SiblingSelector ss = (SiblingSelector) selector;

			if (node != null && node.getParent() != null && matches(ss.getSiblingSelector(), node)) {

				// find next sibling
				final Iterator<INode> i = node.getParent().children().iterator();
				INode e = null;
				INode f = null;
				while (i.hasNext() && e != node) {
					f = e;
					e = i.next();
				}

				if (e == node) {
					return matches(ss.getSelector(), f);
				}
			}
			return false;

		default:
			// System.out.println("DEBUG: selector type not supported");
			// TODO: warning: selector not supported
		}
		return false;
	}

	private static String getLocalNameOfElement(final INode node) {
		return node.accept(new BaseNodeVisitorWithResult<String>("") {
			@Override
			public String visit(final IElement element) {
				return element.getLocalName();
			}

			@Override
			public String visit(final IComment comment) {
				return CSS.XML_COMMENT;
			}

			@Override
			public String visit(final IProcessingInstruction pi) {
				return CSS.XML_PROCESSING_INSTRUCTION;
			}
		});
	}

	private static String getNamespaceURIOfElement(final INode node) {
		return node.accept(new BaseNodeVisitorWithResult<String>("") {
			@Override
			public String visit(final IElement element) {
				return element.getQualifiedName().getQualifier();
			}

			@Override
			public String visit(final IComment comment) {
				return Namespace.VEX_NAMESPACE_URI;
			}

			@Override
			public String visit(final IProcessingInstruction pi) {
				return Namespace.VEX_NAMESPACE_URI;
			}
		});
	}

	/**
	 * Returns true if some ancestor of the given element matches the given selector.
	 */
	private static boolean matchesAncestor(final Selector selector, final INode node) {
		for (final INode ancestor : node.ancestors()) {
			if (matches(selector, ancestor)) {
				return true;
			}
		}
		return false;
	}

	private static boolean matchesCondition(final Condition condition, final INode node) {

		AttributeCondition attributeCondition;
		String attributeName;
		String value;

		switch (condition.getConditionType()) {
		case Condition.SAC_PSEUDO_CLASS_CONDITION:
			return false;

		case Condition.SAC_ATTRIBUTE_CONDITION:
			attributeCondition = (AttributeCondition) condition;
			value = getAttributeValue(node, attributeCondition.getLocalName());
			if (attributeCondition.getValue() != null) {
				return attributeCondition.getValue().equals(value);
			} else {
				return value != null;
			}

		case Condition.SAC_ONE_OF_ATTRIBUTE_CONDITION:
		case Condition.SAC_CLASS_CONDITION:

			attributeCondition = (AttributeCondition) condition;

			if (condition.getConditionType() == Condition.SAC_CLASS_CONDITION) {
				attributeName = "class";
			} else {
				attributeName = attributeCondition.getLocalName();
			}

			value = getAttributeValue(node, attributeName);
			if (value == null) {
				return false;
			}
			final StringTokenizer st = new StringTokenizer(value);
			while (st.hasMoreTokens()) {
				if (st.nextToken().equals(attributeCondition.getValue())) {
					return true;
				}
			}
			return false;

		case Condition.SAC_AND_CONDITION:
			final CombinatorCondition ccon = (CombinatorCondition) condition;
			return matchesCondition(ccon.getFirstCondition(), node) && matchesCondition(ccon.getSecondCondition(), node);

		default:
			// TODO: warning: condition not supported
			System.out.println("Unsupported condition type: " + condition.getConditionType());
		}
		return false;
	}

	private static String getAttributeValue(final INode node, final String localName) {
		return node.accept(new BaseNodeVisitorWithResult<String>() {
			@Override
			public String visit(final IElement element) {
				return element.getAttributeValue(localName);
			}
		});
	}

	/**
	 * Calculates the specificity for a selector.
	 */
	private static int specificity(final Selector sel) {
		if (sel instanceof ElementSelector) {
			if (((ElementSelector) sel).getLocalName() == null) {
				// actually wildcard selector -- see comment in matches()
				return 0;
			} else {
				return 1;
			}
		} else if (sel instanceof DescendantSelector) {
			final DescendantSelector ds = (DescendantSelector) sel;
			return specificity(ds.getAncestorSelector()) + specificity(ds.getSimpleSelector());
		} else if (sel instanceof SiblingSelector) {
			final SiblingSelector ss = (SiblingSelector) sel;
			return specificity(ss.getSelector()) + specificity(ss.getSiblingSelector());
		} else if (sel instanceof NegativeSelector) {
			final NegativeSelector ns = (NegativeSelector) sel;
			return specificity(ns.getSimpleSelector());
		} else if (sel instanceof ConditionalSelector) {
			final ConditionalSelector cs = (ConditionalSelector) sel;
			return specificity(cs.getCondition()) + specificity(cs.getSimpleSelector());
		} else {
			return 0;
		}
	}

	/**
	 * Calculates the specificity for a condition.
	 */
	private static int specificity(final Condition cond) {
		if (cond instanceof CombinatorCondition) {
			final CombinatorCondition cc = (CombinatorCondition) cond;
			return specificity(cc.getFirstCondition()) + specificity(cc.getSecondCondition());
		} else if (cond instanceof AttributeCondition) {
			if (cond.getConditionType() == Condition.SAC_ID_CONDITION) {
				return 1000000;
			} else {
				return 1000;
			}
		} else {
			return 0;
		}
	}
}
