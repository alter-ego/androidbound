package com.alterego.androidbound;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.IViewResolver;

public class ChainedViewResolver implements IViewResolver {
	private List<IViewResolver> mBaseViewResolvers;

	public ChainedViewResolver() {
		mBaseViewResolvers = new ArrayList<IViewResolver>();
	}
	public ChainedViewResolver(IViewResolver...initialViewResolvers) {
		this();
		if(initialViewResolvers == null) {
			return;
		}

		for(IViewResolver r: initialViewResolvers) {
			mBaseViewResolvers.add(r);
		}
	}

	@Override
	public void setLogger(IAndroidLogger logger) {
	}

	@Override
	public View createView(String name, Context context, AttributeSet attrs) {
		for(IViewResolver resolver: mBaseViewResolvers) {
			View view = resolver.createView(name, context, attrs);
			if(view != null) {
				return view;
			}
		}

		return null;
	}

	public void addResolverToFront(IViewResolver resolver) {
		mBaseViewResolvers.add(0, resolver);
	}

	public void addResolverToBack(IViewResolver resolver) {
		mBaseViewResolvers.add(resolver);
	}

	public void removeResolver(IViewResolver resolver) {
		mBaseViewResolvers.remove(resolver);
	}
}
