package solutions.alterego.androidbound.android.interfaces;

import android.view.LayoutInflater;

public interface IBindableLayoutInflaterFactory {
	public abstract LayoutInflater.Factory inflaterFor(Object source);
	public abstract LayoutInflater.Factory inflaterFor(Object source, LayoutInflater.Factory factory);
	public abstract LayoutInflater.Factory inflaterFor(Object source, LayoutInflater.Factory2 factory2);
}