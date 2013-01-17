package com.jsera.util;

import java.awt.Graphics;

public abstract class World implements Updatable {
	protected int ID;
	protected static UIDHandler oUID = new UIDHandler();
	protected String[] aImages;

	public World() {
		this.ID = oUID.getUID();
	}

	@Override
	public void finalize() {
		oUID.releaseUID(this.ID);
	}

	public boolean equals(World o) {
		boolean result = false;
		if (o.ID == this.ID) {
			result = true;
		}
		return result;
	}

	@Override
	public void update(float elapsed) {
	}

	@Override
	public void onEnter() {
	}

	@Override
	public void onExit() {
	}

	@Override
	public void onPlay() {
	}

	@Override
	public void onPause() {
	}

	public void paint(Graphics g) {
	}

	public void init() {
	}
}
