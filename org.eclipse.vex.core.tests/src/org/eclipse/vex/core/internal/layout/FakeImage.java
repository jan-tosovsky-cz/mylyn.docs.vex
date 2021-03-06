package org.eclipse.vex.core.internal.layout;

import java.net.URL;

import org.eclipse.vex.core.internal.core.Image;

public class FakeImage implements Image {
	public final URL url;
	private final int height;
	private final int width;

	public FakeImage(final URL url) {
		this(url, 0, 0);
	}

	public FakeImage(final URL url, final int width, final int height) {
		this.url = url;
		this.height = height;
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public String toString() {
		return "FakeImage [url=" + url + ", height=" + height + ", width=" + width + "]";
	}

}
