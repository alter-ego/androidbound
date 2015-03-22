package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import solutions.alterego.androidbound.android.ui.BindableListItemView;
import solutions.alterego.androidbound.interfaces.IBindableView;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableExtendedListAdapter extends BaseAdapter {

    private static final int defaultCacheSize = 100;

    private final SparseArray<BindableListItemView> itemViews;

    private final ArrayList<Integer> itemViewsPriorityIndex;

    private List<? extends Object> itemsSource;

    private int mCacheSize;

    private IViewBinder viewBinder;

    private Context context;

    private HashMap<Class<?>, Integer> mTemplatesForObjects;

    public BindableExtendedListAdapter(Context context) {
        this(context, null, defaultCacheSize);
    }

    public BindableExtendedListAdapter(Context context, IViewBinder viewBinder) {
        this(context, viewBinder, defaultCacheSize);
    }

    public BindableExtendedListAdapter(Context context, IViewBinder viewBinder, int cacheSize) {
        this.mCacheSize = cacheSize;
        this.itemViews = new SparseArray<BindableListItemView>();
        this.itemViewsPriorityIndex = new ArrayList<Integer>();
        this.context = context;
        mTemplatesForObjects = new HashMap<Class<?>, Integer>();

        if (viewBinder == null) {
            if ((Object) context instanceof IBindableView) {
                viewBinder = ((IBindableView) (Object) context).getViewBinder();
            }
        }

        this.viewBinder = viewBinder;
    }

    public List<? extends Object> getItemsSource() {
        return itemsSource;
    }

    public void setItemsSource(List<? extends Object> value) {
        if (value == null || itemsSource == null) {
            itemsSource = value;
            this.notifyDataSetChanged();
            return;
        }

        if (this.itemsSource != null) {
            List<Integer> toremovePositions = null;
            for (int i = 0; i < this.itemsSource.size(); i++) {
                Object o = this.itemsSource.get(i);
                int newIndex = value.indexOf(o);
                if (newIndex < 0) {
                    if (toremovePositions == null) {
                        toremovePositions = new ArrayList<Integer>();
                    }
                    toremovePositions.add(i);
                }
            }

            this.removeViews(toremovePositions);
        }

        this.itemsSource = value;

        this.updateViews();

        this.notifyDataSetChanged();
    }

    public int getCount() {
        if (this.itemsSource == null) {
            return 0;
        }

        return itemsSource.size();
    }

    public Object getItem(int position) {
        if (this.itemsSource == null) {
            return null;
        }
        return itemsSource.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (this.itemsSource == null) {
            return null;
        }

        BindableListItemView currentView = this.findView(position);

        if (currentView != null) {
            return currentView;
        }

        try {
            Object source = itemsSource.get(position);
            if (getTemplatesForObjects().containsKey(source.getClass())) {
                currentView = new BindableListItemView(context, viewBinder, getTemplatesForObjects().get(source.getClass()), source);
                this.insertView(position, currentView);
            }
        } catch (Exception e) {
        }
        if (currentView == null) {
            Log.e("DELTATRE", "BindableExtendedListAdapter.getView() returns null");
        }
        return currentView;
    }

    private void insertView(int position, BindableListItemView v) {
        this.itemViews.put(position, v);

        this.itemViewsPriorityIndex.remove((Object) position);
        this.itemViewsPriorityIndex.add(0, position);

        while (this.itemViewsPriorityIndex.size() > mCacheSize) {
            int indexToRemove = this.itemViewsPriorityIndex.remove(this.itemViewsPriorityIndex.size() - 1);
            this.itemViews.remove(indexToRemove);
        }
    }

    private void removeView(int position) {
        BindableListItemView bv = this.itemViews.get(position);
        this.itemViews.remove(position);
        this.itemViewsPriorityIndex.remove((Object) position);

        if (bv != null) {
            bv.unbind();
        }
    }

    private void removeViews(List<Integer> positions) {
        if (positions == null) {
            return;
        }

        for (Integer pos : positions) {
            this.removeView(pos);
        }
    }

    private BindableListItemView findView(int position) {
        return this.itemViews.get(position);
    }

    /*
    private void invalidateViews() {
    	this.itemViews.clear();
    	this.itemViewsPriorityIndex.clear();
    }
    */

    private void updateViews() {
        if (this.itemsSource == null) {
            return;
        }

        for (int i = 0; i < this.itemsSource.size(); i++) {
            BindableListItemView bv = this.itemViews.get(i);
            if (bv != null) {
                bv.bindTo(this.itemsSource.get(i));
            }
        }
    }

    public void setCacheSize(int cacheSize) {
        mCacheSize = cacheSize;
    }

    public HashMap<Class<?>, Integer> getTemplatesForObjects() {
        return mTemplatesForObjects;
    }

    public void setTemplatesForObjects(HashMap<Class<?>, Integer> map) {
        mTemplatesForObjects = map;
    }
}
