package com.alterego.androidbound.android.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alterego.androidbound.ViewBinder;
import com.alterego.androidbound.android.ui.BindableListItemView;
import com.alterego.androidbound.interfaces.IBindableView;
import com.alterego.androidbound.interfaces.IBindingAssociation;
import com.alterego.androidbound.interfaces.IViewBinder;

import java.util.ArrayList;
import java.util.List;

public class BindableListAdapter extends BaseAdapter {

    private static final int defaultCacheSize = 100;

    private int itemTemplate;

    private List<? extends Object> itemsSource;

    private int mCacheSize;

    private final SparseArray<View> itemViews;

    private final ArrayList<Integer> itemViewsPriorityIndex;

    protected IViewBinder viewBinder;

    protected Context context;

    public BindableListAdapter(Context context) {
        this(context, null, defaultCacheSize);
    }

    public BindableListAdapter(Context context, IViewBinder viewBinder) {
        this(context, viewBinder, defaultCacheSize);
    }

    public BindableListAdapter(Context context, IViewBinder viewBinder, int cacheSize) {
        mCacheSize = cacheSize;
        this.itemViews = new SparseArray<View>();

        this.itemViewsPriorityIndex = new ArrayList<Integer>();
        this.context = context;

        if (viewBinder == null) {
            if ((Object)context instanceof IBindableView) {
                viewBinder = ((IBindableView)(Object)context).getViewBinder();
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

        //this.mCacheSize = value.size();
        this.itemsSource = value;

        this.updateViews();

        this.notifyDataSetChanged();
    }

    public int getItemTemplate() {
        return itemTemplate;
    }

    public void setItemTemplate(int id) {
        itemTemplate = id;
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

        //View currentView = null;

        View currentView = this.findView(position);

        //we already have the view
        if (currentView != null) {
            ViewBinder.getLogger().debug("BindableListAdapter getView currentView != null");
            return currentView;
        }

        //        BindableListItemView recycleView = null;
        //
        //        if (convertView instanceof BindableListItemView) {
        //            for (int i = 0; i < this.itemViews.size(); i++) {
        //                BindableListItemView valAt = this.itemViews.valueAt(i);
        //                int posAt = this.itemViews.keyAt(i);
        //
        //                if (valAt == convertView) {
        //                    if (posAt != position) {
        //                        this.removeView(this.itemViews.keyAt(i));
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
                this.insertView(position, currentView);
//            }


            //if (recycleView != null) {
            //    currentView = recycleView;
            //    currentView.bindTo(source);
            //} else {
            //currentView = new BindableListItemView(context, viewBinder, itemTemplate, source);
            //}

            //this.insertView(position, currentView);
        } catch (Exception e) {
            //we do nothing
        }
        return currentView;
    }

    protected void insertView(int position, View v) {
        this.itemViews.put(position, v);

        this.itemViewsPriorityIndex.remove((Object)position);
        this.itemViewsPriorityIndex.add(0, position);

        while (this.itemViewsPriorityIndex.size() > mCacheSize) {
            int indexToRemove = this.itemViewsPriorityIndex.remove(this.itemViewsPriorityIndex.size() - 1);
            this.itemViews.remove(indexToRemove);
        }
    }

    private void removeView(int position) {
    	View v = this.itemViews.get(position);
        this.itemViews.remove(position);
        this.itemViewsPriorityIndex.remove((Object)position);

        if(v != null) {
        	this.unbindView(v);
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

    protected View findView(int position) {
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

        for(int i = 0; i < this.itemsSource.size(); i++) {
        	View v = this.itemViews.get(i);
        	if(v != null) {
        		this.bindViewTo(v, this.itemsSource.get(i));
        	} else {
                v = new BindableListItemView(context, viewBinder, itemTemplate, this.itemsSource.get(i));
                this.insertView(i, v);
            }
        }
    }

    private View createView(Object source) {
    	return this.viewBinder.inflate(this.context, source, this.itemTemplate, null);
    }
    
    private void bindViewTo(View view, Object source) {
    	List<IBindingAssociation> bindings = this.viewBinder.getBindingsForViewAndChildren(view);
        if (bindings == null || bindings.size() < 1) {
            return;
        }

        for (IBindingAssociation binding : bindings) {
            binding.setDataContext(source);
        }

        view.invalidate();
        view.requestLayout();
    }
    
    private void unbindView(View view) {
        this.viewBinder.clearBindingForViewAndChildren(view);
    }

    public void setCacheSize(int cacheSize) {
        mCacheSize = cacheSize;
    }
}
