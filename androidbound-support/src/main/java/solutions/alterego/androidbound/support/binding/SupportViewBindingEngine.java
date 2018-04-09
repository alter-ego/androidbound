package solutions.alterego.androidbound.support.binding;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.List;

import solutions.alterego.androidbound.binding.ViewBindingEngine;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.ILogger;

public class SupportViewBindingEngine extends ViewBindingEngine {

    public SupportViewBindingEngine(ILogger logger, boolean debugMode) {
        super(logger, debugMode);
    }

    @Override
    protected List<IBindingAssociationEngine> getBindingsForViewAndChildrenRecursive(View rootView, List<IBindingAssociationEngine> bindings) {
        if (mBoundViews.containsKey(rootView)) {
            bindings.addAll(mBoundViews.get(rootView));
        }

        if (!(rootView instanceof ViewGroup)) {
            return bindings;
        }

        ViewGroup vg = (ViewGroup) rootView;

        for (int i = 0; i < vg.getChildCount(); i++) {
            View view = vg.getChildAt(i);
            if (view instanceof RecyclerView || view instanceof AbsListView) {
                getBindingsForView(view, bindings);
            } else {
                getBindingsForViewAndChildrenRecursive(view, bindings);
            }
        }
        return bindings;
    }

}
