package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.adapters.BindableRecyclerViewAdapter;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BindableRecyclerView extends RecyclerView implements IBindableView {

    private final int mItemTemplate;

    @Getter
    private Map<Class<?>, Integer> mTemplatesForObjects;

    @Getter
    @Setter
    private IViewBinder mViewBinder;

    @Getter
    private BindableRecyclerViewAdapter mAdapter;

    public BindableRecyclerView(Context context) {
        this(context, null);
    }

    public BindableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BindableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mItemTemplate = getItemTemplate(context, attrs);
        mTemplatesForObjects = new HashMap<>();
    }

    private int getItemTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableListView.itemTemplate, 0);
    }

    public List<?> getItemsSource() {
        if (mAdapter != null) {
            return mAdapter.getItemsSource();
        }
        return null;
    }

    public void setItemsSource(List<?> value) {
        if (mAdapter == null) {
            mAdapter = new BindableRecyclerViewAdapter(getContext(), getViewBinder());
            mAdapter.setItemTemplate(mItemTemplate);
            mAdapter.setTemplatesForObjects(mTemplatesForObjects);
            setAdapter(mAdapter);
        }

        if (getLayoutManager() != null) {
            mAdapter.setLayoutManager(getLayoutManager());
        }

        mAdapter.setItemsSource(value);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof BindableRecyclerViewAdapter) {
            mAdapter = (BindableRecyclerViewAdapter) adapter;
            if (getLayoutManager() != null) {
                mAdapter.setLayoutManager(getLayoutManager());
            }
        }
    }

    public void setTemplatesForObjects(Map<Class<?>, Integer> map) {
        mTemplatesForObjects = map;
        if (mAdapter != null) {
            mAdapter.setTemplatesForObjects(mTemplatesForObjects);
        }
    }

}
