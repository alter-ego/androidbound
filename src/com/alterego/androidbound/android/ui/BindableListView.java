
package com.alterego.androidbound.android.ui;

import com.alterego.androidbound.BindingResources;
import com.alterego.androidbound.android.adapters.BindableListAdapter;
import com.alterego.androidbound.interfaces.IBindableView;
import com.alterego.androidbound.interfaces.ICommand;
import com.alterego.androidbound.interfaces.IViewBinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import java.util.List;
import java.util.Map;

public class BindableListView extends ListView implements OnItemClickListener, OnItemLongClickListener, IBindableView {

    private int listHeaderTemplate;
    private AttributeSet mAttributes;
    private Context mContext;
    private int itemTemplate;

    private ICommand onClick = ICommand.empty;
    private ICommand onLongClick = ICommand.empty;
    private BindableListAdapter adapter;
    private IViewBinder viewBinder;
    public Map<String, Object> Extensions = null;
    private View mHeader;

    public BindableListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mAttributes = attrs;

        itemTemplate = getItemTemplate(context, attrs);
        listHeaderTemplate = getListHeaderTemplate(context, attrs);

        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    public BindableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    public void setItemsSource(List<?> value) {

        if (adapter == null) {
            adapter = new BindableListAdapter(getContext(), getViewBinder());
            adapter.setItemTemplate(itemTemplate);
            adapter.setItemsSource(value);
            setAdapter(adapter);
        } else {
            adapter.setItemsSource(value);
        }
    }

    public List<?> getItemsSource() {
        if (adapter != null) {
            return adapter.getItemsSource();
        }
        return null;
    }

    private static int getItemTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableListView.itemTemplate, 0);
    }

    private static int getListHeaderTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableListView.listHeaderTemplate, 0);
    }

    private static int getListFooterTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableListView.listFooterTemplate, 0);
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

    public Map<String, Object> getExtensions() {
        return Extensions;
    }

    public void setExtensions(Map<String, Object> ext) {
        Extensions = ext;
    }
}
