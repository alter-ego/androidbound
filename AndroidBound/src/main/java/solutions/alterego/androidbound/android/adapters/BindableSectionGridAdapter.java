package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import solutions.alterego.androidbound.android.interfaces.IBindableSectionGridViewReceiver;
import solutions.alterego.androidbound.android.ui.BindableLinearLayout;
import solutions.alterego.androidbound.android.ui.BindableListItemView;
import solutions.alterego.androidbound.interfaces.IBindableView;
import solutions.alterego.androidbound.interfaces.IViewBinder;

//TODO - implement caching with composite binding; it's disabled at the moment

@SuppressWarnings("rawtypes")
public class BindableSectionGridAdapter extends BaseAdapter implements OnClickListener {

    private static final int defaultCacheSize = 100;

    private int itemTemplate;

    private int headerTemplate;

    private int gridTemplate;

    private Class mHeaderObjectClass;

    private Class mItemObjectClass;

    private int mNumberOfItemsInRow = 0;

    private ArrayList<IBindableSectionGridViewReceiver> itemReceivers;

    private List<? extends Object> itemsSource;

    private LinkedHashMap<Object, ArrayList<Object>> categoryAndItemsHashmap;

    private ArrayList<Object> categoryList;

    private HashMap<BindableListItemView, Object> objectsForViews;

    private int mCacheSize;

    //private final SparseArray<BindableLinearLayout> itemViews;

    //private final ArrayList<Integer> itemViewsPriorityIndex;

    private IViewBinder viewBinder;

    private Context context;

    public BindableSectionGridAdapter(Context context) {
        this(context, null, defaultCacheSize);
    }

    public BindableSectionGridAdapter(Context context, IViewBinder viewBinder) {
        this(context, viewBinder, defaultCacheSize);
    }

    public BindableSectionGridAdapter(Context context, IViewBinder viewBinder, int cacheSize) {
        this.mCacheSize = cacheSize;
        //this.itemViews = new SparseArray<BindableLinearLayout>();
        objectsForViews = new HashMap<BindableListItemView, Object>();
        //this.itemViewsPriorityIndex = new ArrayList<Integer>();
        this.context = context;
        itemReceivers = new ArrayList<IBindableSectionGridViewReceiver>();

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

        //        ArrayList<Object> oldCategories = null;
        //        ArrayList<Object> newCategories = null;

        if (value == null) {
            itemsSource = null;
            categoryList = null;
            categoryAndItemsHashmap = null;
            this.notifyDataSetChanged();
            return;
        }

        if (value != null) {
            itemsSource = value;
            expandListIntoInternalStructures(value);
        }

        //        if (value != null) {
        //            itemsSource = value;
        //            if ((categoryAndItemsHashmap != null) && (categoryAndItemsHashmap.size() > 0))
        //                oldCategories = new ArrayList<Object>(categoryAndItemsHashmap.keySet());
        //            expandListIntoInternalStructures(value);
        //            if ((categoryAndItemsHashmap != null) && (categoryAndItemsHashmap.size() > 0))
        //                newCategories = new ArrayList<Object>(categoryAndItemsHashmap.keySet());
        //
        //            try {
        //                if (oldCategories != null && oldCategories.size() > 0 && newCategories != null && newCategories.size() > 0) {
        //                    List<Integer> toremovePositions = null;
        //                    for (int i = 0; i < oldCategories.size(); i++) {
        //                        Object o = oldCategories.get(i);
        //                        int newIndex = newCategories.indexOf(o);
        //                        if (newIndex < 0) {
        //                            if (toremovePositions == null) {
        //                                toremovePositions = new ArrayList<Integer>();
        //                            }
        //                            toremovePositions.add(i);
        //                        }
        //                    }
        //
        //                    this.removeViews(toremovePositions);
        //                }
        //            } catch (Exception e) {
        //                //error removing from cache
        //                e.printStackTrace();
        //            }
        //        }

        //this.updateViews();
        this.notifyDataSetChanged();
    }

    private void expandListIntoInternalStructures(List<? extends Object> source) {

        Object category = null;
        categoryList = new ArrayList<Object>();
        categoryAndItemsHashmap = new LinkedHashMap<Object, ArrayList<Object>>();
        ArrayList<Object> currentCategoryItems = null;

        for (Object item : source) {

            if (item.getClass() == getHeaderObjectClass()) {
                //first we put the category and items into hashmap because we've arrived at the next category
                if (category != null && currentCategoryItems.size() > 0) {
                    categoryAndItemsHashmap.put(category, currentCategoryItems);
                }
                currentCategoryItems = new ArrayList<Object>();
                category = item;
                categoryList.add((Object) category);
            } else if (item.getClass() == getItemObjectClass()) {
                currentCategoryItems.add(item);
            }

        }
        if (category != null) {
            categoryAndItemsHashmap.put(category, currentCategoryItems);
        }

    }

    public int getHeaderTemplate() {
        return headerTemplate;
    }

    public void setHeaderTemplate(int id) {
        headerTemplate = id;
    }

    public int getItemTemplate() {
        return itemTemplate;
    }

    public void setItemTemplate(int id) {
        itemTemplate = id;
    }

    public int getNumberOfItemsInRow() {
        return mNumberOfItemsInRow;
    }

    public void setNumberOfItemsInRow(int id) {
        mNumberOfItemsInRow = id;
    }

    public int getGridTemplate() {
        return gridTemplate;
    }

    public void setGridTemplate(int id) {
        gridTemplate = id;
    }

    public Class getHeaderObjectClass() {
        return mHeaderObjectClass;
    }

    public void setHeaderObjectClass(Class classname) {
        mHeaderObjectClass = classname;
    }

    public Class getItemObjectClass() {
        return mItemObjectClass;
    }

    public void setItemObjectClass(Class classname) {
        mItemObjectClass = classname;
    }

    public int getCount() {
        if (categoryList == null) {
            return 0;
        }

        return categoryList.size();
    }

    public Pair<Object, ArrayList<Object>> getItem(int position) {
        if (this.categoryList == null) {
            return null;
        }

        Object cat = categoryList.get(position);
        ArrayList<Object> items = categoryAndItemsHashmap.get(cat);

        return new Pair<Object, ArrayList<Object>>(cat, items);

    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        BindableLinearLayout currentView;

        if (this.categoryList == null || categoryList.size() == 0) {
            return null;
        }

        Pair<Object, ArrayList<Object>> source = getItem(position);

        //        BindableLinearLayout currentView = this.findView(position);
        //        if (currentView != null) {
        //            return currentView;
        //        }

        currentView = new BindableLinearLayout(context, viewBinder, gridTemplate, source);
        currentView.setOrientation(LinearLayout.VERTICAL);

        explodeGridView(currentView, source);
        //this.insertView(position, currentView);

        return currentView;
    }

    private void explodeGridView(BindableLinearLayout currentView, Pair<Object, ArrayList<Object>> entry) {

        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        //first we instantiate the header
        BindableListItemView headerView = new BindableListItemView(context, viewBinder, headerTemplate, entry.first);
        currentView.addView(headerView, rowLayoutParams);

        //then we instantiate all the child views
        ArrayList<BindableListItemView> childViews = new ArrayList<BindableListItemView>();
        for (Object childEntry : entry.second) {
            BindableListItemView childView = new BindableListItemView(context, viewBinder, itemTemplate, childEntry);
            childView.setOnClickListener(this);
            childViews.add(childView);
            objectsForViews.put(childView, childEntry);
        }

        //now we populate rows with children
        LinearLayout row = newRow(context);
        int itemsInRow = 0;
        for (View child : childViews) {
            row.addView(child, childLayoutParams);
            itemsInRow++;
            if (itemsInRow == getNumberOfItemsInRow()) {
                currentView.addView(row);
                row = newRow(context);
                itemsInRow = 0;
            }
        }

        //we add the last line
        currentView.addView(row, rowLayoutParams);

    }

    private LinearLayout newRow(Context c) {

        LinearLayout row = new LinearLayout(c);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL + Gravity.LEFT;
        row.setLayoutParams(layoutParams);
        row.setOrientation(LinearLayout.HORIZONTAL);
        return row;

    }

    //    private void insertView(int position, BindableLinearLayout v) {
    //        this.itemViews.put(position, v);
    //
    //        this.itemViewsPriorityIndex.remove((Object)position);
    //        this.itemViewsPriorityIndex.add(0, position);
    //
    //        while (this.itemViewsPriorityIndex.size() > mCacheSize) {
    //            int indexToRemove = this.itemViewsPriorityIndex.remove(this.itemViewsPriorityIndex.size() - 1);
    //            this.itemViews.remove(indexToRemove);
    //        }
    //    }
    //
    //    private void removeView(int position) {
    //        if (position < 0) {
    //            return;
    //        }
    //
    //        BindableLinearLayout bv = this.itemViews.get(position);
    //
    //        this.itemViews.remove(position);
    //        this.itemViewsPriorityIndex.remove((Object)position);
    //
    //        if (bv != null) {
    //            bv.unbind();
    //        }
    //    }

    //    private void removeViews(List<Integer> positions) {
    //        if (positions == null) {
    //            return;
    //        }
    //
    //        for (Integer pos : positions) {
    //            this.removeView(pos);
    //        }
    //    }

    //    private BindableLinearLayout findView(int position) {
    //        return this.itemViews.get(position);
    //    }

    //    private void updateViews() {
    //        if (categoryList == null || categoryList.size() == 0) {
    //            return;
    //        }
    //
    //        for (int i = 0; i < categoryList.size(); i++) {
    //            BindableLinearLayout bv = this.itemViews.get(i);
    //            if (bv != null) {
    //                bv.bindTo(getItem(i));
    //            }
    //        }
    //    }

    public void setCacheSize(int cacheSize) {
        mCacheSize = cacheSize;
    }

    private Object findItemForClickedView(View view) {
        if (objectsForViews.containsKey(view)) {
            return objectsForViews.get(view);
        } else {
            return null;
        }
    }

    public void registerBindableSectionGridViewReceiver(IBindableSectionGridViewReceiver receiver) {
        if (receiver != null) {
            itemReceivers.add(receiver);
        }
    }

    private void notifyReceivers(Object obj) {
        for (IBindableSectionGridViewReceiver receiver : itemReceivers) {
            receiver.receiveObject(obj);
        }
    }

    public void deregisterBindableSectionGridViewReceiver(IBindableSectionGridViewReceiver receiver) {
        if (receiver != null && itemReceivers.contains(receiver)) {
            itemReceivers.remove(receiver);
        }
    }

    @Override
    public void onClick(View view) {
        //String msg;
        Object item = findItemForClickedView(view);
        //msg = "onClick view = " + view + ", item class = " + item.getClass();
        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        notifyReceivers(item);
    }
}
