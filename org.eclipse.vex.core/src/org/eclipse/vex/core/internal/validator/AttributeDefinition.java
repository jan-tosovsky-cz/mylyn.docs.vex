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
package org.eclipse.vex.core.internal.validator;

import java.io.ObjectStreamException;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;

/**
 * <code>AttributeDefinition</code> represents an attribute definition in a Grammar.
 */
public class AttributeDefinition implements Comparable<AttributeDefinition> {

	private final String name;
	private final Type type;
	private final String defaultValue;
	private final String[] values;
	private final boolean required;
	private final boolean fixed;

	/**
	 * Enumeration of attribute types.
	 * 
	 */
	public static final class Type {

		private final String s;

		public static final Type CDATA = new Type("CDATA");
		public static final Type ID = new Type("ID");
		public static final Type IDREF = new Type("IDREF");
		public static final Type IDREFS = new Type("IDREFS");
		public static final Type NMTOKEN = new Type("NMTOKEN");
		public static final Type NMTOKENS = new Type("NMTOKENS");
		public static final Type ENTITY = new Type("ENTITY");
		public static final Type ENTITIES = new Type("ENTITIES");
		public static final Type NOTATION = new Type("NOTATION");
		public static final Type ENUMERATION = new Type("ENUMERATION");

		private Type(final String s) {
			this.s = s;
		}

		public static Type get(final String s) {
			if (s.equals(CDATA.toString())) {
				return CDATA;
			} else if (s.equals(ID.toString())) {
				return ID;
			} else if (s.equals(IDREF.toString())) {
				return IDREF;
			} else if (s.equals(IDREFS.toString())) {
				return IDREFS;
			} else if (s.equals(NMTOKEN.toString())) {
				return NMTOKEN;
			} else if (s.equals(NMTOKENS.toString())) {
				return NMTOKENS;
			} else if (s.equals(ENTITY.toString())) {
				return ENTITY;
			} else if (s.equals(ENTITIES.toString())) {
				return ENTITIES;
			} else if (s.equals(NOTATION.toString())) {
				return NOTATION;
			} else if (s.equals(ENUMERATION.toString()) || s.equals(CMDataType.ENUM)) {
				return ENUMERATION;
			} else {
				System.out.println("Found unknown attribute type '" + s + "'.");
				return CDATA;
			}
		}

		@Override
		public String toString() {
			return s;
		}

		/**
		 * Serialization method, to ensure that we do not introduce new instances.
		 */
		private Object readResolve() throws ObjectStreamException {
			return get(toString());
		}
	}

	/**
	 * Class constructor.
	 */
	public AttributeDefinition(final String name, final Type type, final String defaultValue, final String[] values, final boolean required, final boolean fixed) {

		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.values = values;
		this.required = required;
		this.fixed = fixed;
	}

	/**
	 * Implements <code>Comparable.compareTo</code> to sort alphabetically by name.
	 * 
	 * @param other
	 *            The attribute to which this one is to be compared.
	 * 
	 */
	public int compareTo(final AttributeDefinition other) {
		return name.compareTo(other.name);
	}

	/**
	 * Returns the attribute's type.
	 * 
	 * @model
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the default value of the attribute.
	 * 
	 * @model
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Returns true if the attribute value is fixed.
	 * 
	 * @model
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * Returns the name of the attribute.
	 * 
	 * @model
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns true if the attribute is required.
	 * 
	 * @model
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Returns an array of acceptable values for the attribute. If null is returned, any value is acceptable for the
	 * attribute.
	 * 
	 * @model type="String" containment="true"
	 */
	public String[] getValues() {
		return values;
	}

}
