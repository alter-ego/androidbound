package solutions.alterego.androidbound.android.interfaces;

import android.view.LayoutInflater;

public interface IBindableLayoutInflaterFactory {

    LayoutInflater.Factory inflaterFor(Object source);

    LayoutInflater.Factory inflaterFor(Object source, LayoutInflater.Factory factory);

    LayoutInflater.Factory2 inflaterFor(Object source, LayoutInflater.Factory2 factory2);
}