package solutions.alterego.androidbound.android;

import android.view.LayoutInflater;

import solutions.alterego.androidbound.android.interfaces.IBindableLayoutInflaterFactory;

public class NullBindableLayoutInflaterFactory implements IBindableLayoutInflaterFactory {

    public static final IBindableLayoutInflaterFactory instance = new NullBindableLayoutInflaterFactory();

    @Override
    public LayoutInflater.Factory inflaterFor(Object source) {
        return null;
    }

    @Override
    public LayoutInflater.Factory inflaterFor(Object source, LayoutInflater.Factory factory) {
        return null;
    }

    @Override
    public LayoutInflater.Factory2 inflaterFor(Object source, LayoutInflater.Factory2 factory2) {
        return null;
    }
}
