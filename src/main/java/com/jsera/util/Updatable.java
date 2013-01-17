package com.jsera.util;

public interface Updatable {
	public abstract void update(float paramFloat);

	public abstract void onEnter();

	public abstract void onExit();

	public abstract void onPlay();

	public abstract void onPause();
}