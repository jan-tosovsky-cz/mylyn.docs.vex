/*******************************************************************************
 * Copyright (c) 2010 Florian Thienel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 		Florian Thienel - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.ui.internal.config.tests;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.vex.ui.internal.config.PluginProject;
import org.eclipse.vex.ui.internal.config.PluginProjectNature;

/**
 * @author Florian Thienel
 */
public class PluginProjectTest {

	public static final String CSS_FILE_NAME = "plugintest.css";
	public static final String CSS_ID = "plugintest";
	public static final String DTD_FILE_NAME = "plugintest.dtd";
	public static final String DTD_DOCTYPE_ID = "-//Vex//Plugin Test//EN";

	public static IProject createVexPluginProject(final String name) throws CoreException {
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if (project.exists()) {
			project.delete(true, true, null);
		}
		project.create(null);
		project.open(null);
		project.getFile(DTD_FILE_NAME).create(new ByteArrayInputStream(new byte[0]), true, null);
		project.getFile(CSS_FILE_NAME).create(new ByteArrayInputStream(new byte[0]), true, null);
		createVexPluginFile(project);
		addVexProjectNature(project);
		return project;
	}

	public static void createVexPluginFile(final IProject project) throws CoreException {
		final String fileContent = createVexPluginFileContent(project);
		writePluginFile(project, fileContent, false);
	}

	public static String createVexPluginFileContent(final IProject project) {
		return createVexPluginFileContent(project, DTD_FILE_NAME, CSS_FILE_NAME);
	}

	public static String createVexPluginFileContent(final IProject project, final String dtdFilename, final String... styleFilenames) {
		final StringWriter result = new StringWriter();
		final PrintWriter out = new PrintWriter(result);

		out.println("<extension id=\"plugintest\" name=\"" + DTD_FILE_NAME + "\" point=\"org.eclipse.vex.ui.doctypes\">"); //$NON-NLS-1$ //$NON-NLS-2$
		out.println("<doctype systemId=\"" + dtdFilename + "\" dtd=\"" + dtdFilename + "\" publicId=\"" + DTD_DOCTYPE_ID + "\" />"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		out.println("</extension>"); //$NON-NLS-1$
		for (final String styleFilename : styleFilenames) {
			out.println("<extension id=\"plugintest\" name=\"plugin test style\" point=\"org.eclipse.vex.ui.styles\">"); //$NON-NLS-1$
			out.println("<style css=\"" + styleFilename + "\"><doctypeRef publicId=\"" + DTD_DOCTYPE_ID + "\" /></style>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			out.println("</extension>"); //$NON-NLS-1$
		}

		out.close();
		return result.toString();
	}

	public static void writePluginFile(final IProject project, final String content, final boolean replace) throws CoreException {
		final StringWriter result = new StringWriter();
		final PrintWriter out = new PrintWriter(result);
		out.println("<?xml version='1.0'?>");
		// HINT: It is important to set the id attribute, because this is used as the unique identifier for the configuration.
		out.println("<plugin id=\"" + project.getName() + "\">");
		out.print(content);
		out.println("</plugin>"); //$NON-NLS-1$
		out.close();
		if (!replace) {
			project.getFile(PluginProject.PLUGIN_XML).create(new ByteArrayInputStream(result.toString().getBytes()), true, null);
		} else {
			project.getFile(PluginProject.PLUGIN_XML).setContents(new ByteArrayInputStream(result.toString().getBytes()), true, true, null);
		}
	}

	public static void addVexProjectNature(final IProject project) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final String[] natures = description.getNatureIds();
		final String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = PluginProjectNature.ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

}
