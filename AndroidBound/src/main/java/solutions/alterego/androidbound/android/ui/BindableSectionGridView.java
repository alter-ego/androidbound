package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import java.util.List;

import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.android.adapters.BindableSectionGridAdapter;
import solutions.alterego.androidbound.android.interfaces.IBindableSectionGridViewReceiver;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@SuppressWarnings("rawtypes")
public class BindableSectionGridView extends GridView implements OnItemClickListener, OnItemLongClickListener, IBindableView {

    private int itemTemplate;

    private int gridTemplate;

    private int headerTemplate;

    private Class mHeaderObjectClass;

    private Class mItemObjectClass;

    private int numberOfItemsInRow = 0;

    private ICommand onClick = ICommand.empty;

    private ICommand onLongClick = ICommand.empty;

    private BindableSectionGridAdapter adapter;

    private IViewBinder viewBinder;

    public BindableSectionGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        itemTemplate = getItemTemplate(context, attrs);
        gridTemplate = getGridTemplate(context, attrs);
        headerTemplate = getHeaderTemplate(context, attrs);
        numberOfItemsInRow = getNumberOfItemsInRow(context, attrs);
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    public BindableSectionGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private static int getHeaderTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableSectionListView.headerTemplate, 0);
    }

    private static int getItemTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableSectionGridView.itemTemplate, 0);
    }

    private static int getGridTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableSectionGridView.gridTemplate, 0);
    }

    private static int getNumberOfItemsInRow(Context context, AttributeSet attrs) {
        return attrs.getAttributeIntValue(null, BindingResources.attr.BindableSectionGridView.numberOfItemsInRow, 2);
    }

    @Override
    public IViewBinder getViewBinder() {
        return this.viewBinder;
    }

    @Override
    public void setViewBinder(IViewBinder viewBinder) {
        this.viewBinder = viewBinder;
    }

    public ICommand getClick() {
        return onClick;
    }

    public void setClick(ICommand value) {
        onClick = value;
    }

    public ICommand getLongClick() {
        return onLongClick;
    }

    public void setLongClick(ICommand value) {
        onLongClick = value;
    }

    public List<? extends Object> getItemsSource() {
        if (adapter != null) {
            return adapter.getItemsSource();
        }
        return null;
    }

    public void setItemsSource(List<?> value) {
        if (adapter == null) {
            adapter = new BindableSectionGridAdapter(getContext(), getViewBinder());
            adapter.setHeaderTemplate(headerTemplate);
            adapter.setItemTemplate(itemTemplate);
            adapter.setGridTemplate(gridTemplate);
            adapter.setHeaderObjectClass(mHeaderObjectClass);
            adapter.setItemObjectClass(mItemObjectClass);
            adapter.setItemsSource(value);
            adapter.setNumberOfItemsInRow(numberOfItemsInRow);
            setAdapter(adapter);
        } else {
            adapter.setItemsSource(value);
        }
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Object parameter = getItemsSource().get(position);
        if (onLongClick.canExecute(parameter)) {
            onLongClick.execute(parameter);
            return true;
        }
        return false;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object parameter = getItemsSource().get(position);
        if (onClick.canExecute(parameter)) {
            onClick.execute(parameter);
        }
    }

    public Class getHeaderObjectClass() {
        return mHeaderObjectClass;
    }

    public void setHeaderObjectClass(Class classname) {
        mHeaderObjectClass = classname;
        if (adapter != null) {
            adapter.setHeaderObjectClass(mHeaderObjectClass);
        }
    }

    public Class getItemObjectClass() {
        return mItemObjectClass;
    }

    public void setItemObjectClass(Class classname) {
        mItemObjectClass = classname;
        if (adapter != null) {
            adapter.setItemObjectClass(mItemObjectClass);
        }
    }

    public int getNumberOfItemsInRow() {
        return numberOfItemsInRow;
    }

    public void setNumberOfItemsInRow(int id) {
        numberOfItemsInRow = id;
    }

    public void registerBindableSectionGridViewReceiver(IBindableSectionGridViewReceiver receiver) {
        if (receiver != null && adapter != null) {
            adapter.registerBindableSectionGridViewReceiver(receiver);
        }
    }

    public void deregisterBindableSectionGridViewReceiver(IBindableSectionGridViewReceiver receiver) {
        if (receiver != null && adapter != null) {
            adapter.deregisterBindableSectionGridViewReceiver(receiver);
        }
    }

}
