/*******************************************************************************
 * Copyright (c) 2004, 2008 John Krasnay and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     John Krasnay - initial API and implementation
 *******************************************************************************/
package org.eclipse.vex.ui.internal;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.vex.core.internal.core.DisplayDevice;
import org.eclipse.vex.core.internal.widget.swt.SwtDisplayDevice;
import org.eclipse.vex.ui.internal.config.ConfigLoaderJob;
import org.eclipse.vex.ui.internal.config.ConfigurationRegistry;
import org.eclipse.vex.ui.internal.config.ConfigurationRegistryImpl;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class VexPlugin extends AbstractUIPlugin {

	public static final String ID = "org.eclipse.vex.ui"; //$NON-NLS-1$

	private static VexPlugin instance;

	private final ConfigurationRegistry configurationRegistry = new ConfigurationRegistryImpl(new ConfigLoaderJob());

	private VexPreferences preferences = null;

	/**
	 * Returns the shared instance.
	 */
	public static VexPlugin getDefault() {
		return instance;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Log an error message without an exception.
	 *
	 * @param severity
	 *            One of the IStatus severity levels, e.g. IStatus.ERROR.
	 * @param message
	 *            Message describing the error.
	 */
	public void log(final int severity, final String message) {
		getLog().log(new Status(severity, ID, 0, message, null));
	}

	/**
	 * Log an error message.
	 *
	 * @param severity
	 *            One of the IStatus severity levels, e.g. IStatus.ERROR.
	 * @param message
	 *            Message describing the error.
	 * @param exception
	 *            exception related to the error, or null if none.
	 */
	public void log(final int severity, final String message, final Throwable exception) {
		getLog().log(new Status(severity, ID, 0, message, exception));
	}

	public ConfigurationRegistry getConfigurationRegistry() {
		return configurationRegistry;
	}

	public VexPreferences getPreferences() {
		if (preferences == null) {
			preferences = new VexPreferences(getPreferenceStore(), getConfigurationRegistry());
		}
		return preferences;
	}

	/**
	 * Override the plugin startup to intialize the resource tracker.
	 */
	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		if (instance != null) {
			throw new IllegalStateException("This plug-in must be a singleton.");
		}
		instance = this;

		// TODO Remove DisplayDevice.setCurrent from VexPlugin.start
		// This has been added to the VexWidget ctor, but the problem is that
		// when loading an editor, we load the document before creating the
		// widget, and to do that we need to load the stylesheet, and *this*
		// needs the DisplayDevice to be set properly.
		//
		// One solution might be to do a simplified stylesheet load that only
		// looks at the display property, which is enough to do space
		// normalization but doesn't need to look at the display device.

		DisplayDevice.setCurrent(new SwtDisplayDevice());

		configurationRegistry.loadConfigurations();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}

}
