package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.ui.BindableListItemView;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableListAdapter extends BaseAdapter {

    private static final int defaultCacheSize = 100;

    private final SparseArray<View> itemViews;

    private final ArrayList<Integer> itemViewsPriorityIndex;

    protected IViewBinder viewBinder;

    protected Context context;

    private int itemTemplate;

    private List<? extends Object> itemsSource;

    private int mCacheSize;

    public BindableListAdapter(Context context) {
        this(context, null, defaultCacheSize);
    }

    public BindableListAdapter(Context ctx, IViewBinder viewBinder) {
        this(ctx, viewBinder, defaultCacheSize);
    }

    public BindableListAdapter(Context ctx, IViewBinder vb, int cacheSize) {
        mCacheSize = cacheSize;
        itemViews = new SparseArray<View>();

        itemViewsPriorityIndex = new ArrayList<Integer>();
        context = ctx;

        viewBinder = vb;

        if (viewBinder == null) {
            if (context instanceof IBindableView) {
                viewBinder = ((IBindableView) context).getViewBinder();
            }
        }
    }

    public List<?> getItemsSource() {
        return itemsSource;
    }

    public void setItemsSource(List<?> value) {
        if (value == null || itemsSource == null) {
            itemsSource = value;
            notifyDataSetChanged();
            return;
        }

        if (itemsSource != null) {
            List<Integer> toremovePositions = null;
            for (int i = 0; i < itemsSource.size(); i++) {
                Object o = itemsSource.get(i);
                int newIndex = value.indexOf(o);
                if (newIndex < 0) {
                    if (toremovePositions == null) {
                        toremovePositions = new ArrayList<Integer>();
                    }
                    toremovePositions.add(i);
                }
            }

            removeViews(toremovePositions);
        }

        //mCacheSize = value.size();
        itemsSource = value;

        updateViews();

        notifyDataSetChanged();
    }

    public int getItemTemplate() {
        return itemTemplate;
    }

    public void setItemTemplate(int id) {
        itemTemplate = id;
    }

    public int getCount() {
        if (itemsSource == null) {
            return 0;
        }

        return itemsSource.size();
    }

    public Object getItem(int position) {
        if (itemsSource == null) {
            return null;
        }
        return itemsSource.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (itemsSource == null) {
            return null;
        }

        //View currentView = null;

        View currentView = findView(position);

        //we already have the view
        if (currentView != null) {
            ViewBinder.getLogger().debug("BindableListAdapter getView currentView != null");
            return currentView;
        }

        //        BindableListItemView recycleView = null;
        //
        //        if (convertView instanceof BindableListItemView) {
        //            for (int i = 0; i < itemViews.size(); i++) {
        //                BindableListItemView valAt = itemViews.valueAt(i);
        //                int posAt = itemViews.keyAt(i);
        //
        //                if (valAt == convertView) {
        //                    if (posAt != position) {
        //                        removeView(itemViews.keyAt(i));
        //                        recycleView = (BindableListItemView) convertView;
        //                    }
        //                    break;
        //                }
        //            }
        //        }





        /*
        if(convertView instanceof BindableListItemView) {
        	currentView = (BindableListItemView)convertView;
        	if(currentView.getItemTemplate() != itemTemplate)
        		currentView = null;
        }
        */
        try {
            Object source = itemsSource.get(position);

            //we have the same view instance, we need to clear the bindings, remove the view, and rebind it
            //if (convertView!=null && convertView instanceof BindableListItemView) {
////                ViewBinder.getLogger().debug("BindableListAdapter getView convertView != null, unbinding");
////                ((BindableListItemView) convertView).unbind();
////                ViewBinder.getLogger().debug("BindableListAdapter getView convertView != null, removing views");
////                ((BindableListItemView) convertView).removeAllViews();
//                ViewBinder.getLogger().debug("BindableListAdapter getView convertView != null, binding with source again");
//                ((BindableListItemView) convertView).bindTo(source);

//                bindViewTo(convertView, source);
//                currentView = convertView;
//            } else { //create the view ex-novo
            ViewBinder.getLogger().debug("BindableListAdapter getView inflate view from zero");
            currentView = new BindableListItemView(context, viewBinder, itemTemplate, source);
            insertView(position, currentView);
//            }

            //if (recycleView != null) {
            //    currentView = recycleView;
            //    currentView.bindTo(source);
            //} else {
            //currentView = new BindableListItemView(context, viewBinder, itemTemplate, source);
            //}

            //insertView(position, currentView);
        } catch (Exception e) {
            //we do nothing
        }
        return currentView;
    }

    protected void insertView(int position, View v) {
        itemViews.put(position, v);

        itemViewsPriorityIndex.remove((Object) position);
        itemViewsPriorityIndex.add(0, position);

        while (itemViewsPriorityIndex.size() > mCacheSize) {
            int indexToRemove = itemViewsPriorityIndex.remove(itemViewsPriorityIndex.size() - 1);
            itemViews.remove(indexToRemove);
        }
    }

    private void removeView(int position) {
        View v = itemViews.get(position);
        itemViews.remove(position);
        itemViewsPriorityIndex.remove((Object) position);

        if (v != null) {
            unbindView(v);
        }
    }

    private void removeViews(List<Integer> positions) {
        if (positions == null) {
            return;
        }

        for (Integer pos : positions) {
            removeView(pos);
        }
    }

    protected View findView(int position) {
        return itemViews.get(position);
    }

    /*
    private void invalidateViews() {
    	itemViews.clear();
    	itemViewsPriorityIndex.clear();
    }
    */

    private void updateViews() {
        if (itemsSource == null) {
            return;
        }

        for (int i = 0; i < itemsSource.size(); i++) {
            View v = itemViews.get(i);
            if (v != null) {
                bindViewTo(v, itemsSource.get(i));
            } else {
                v = new BindableListItemView(context, viewBinder, itemTemplate, itemsSource.get(i));
                insertView(i, v);
            }
        }
    }

    private View createView(Object source) {
        return viewBinder.inflate(context, source, itemTemplate, null);
    }

    private void bindViewTo(View view, Object source) {
        List<IBindingAssociationEngine> bindings = viewBinder.getBindingsForViewAndChildren(view);
        if (bindings == null || bindings.size() < 1) {
            return;
        }

        for (IBindingAssociationEngine binding : bindings) {
            binding.setDataContext(source);
        }

        view.invalidate();
        view.requestLayout();
    }

    private void unbindView(View view) {
        viewBinder.clearBindingForViewAndChildren(view);
    }

    public void setCacheSize(int cacheSize) {
        mCacheSize = cacheSize;
    }
}
