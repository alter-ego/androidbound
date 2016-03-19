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
import solutions.alterego.androidbound.android.adapters.BindableListAdapter;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableGridView extends GridView implements OnItemClickListener, OnItemLongClickListener, IBindableView {

    private int itemTemplate;

    private ICommand onClick = ICommand.empty;

    private ICommand onLongClick = ICommand.empty;

    private BindableListAdapter adapter;

    private IViewBinder viewBinder;

    public BindableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        itemTemplate = getItemTemplate(context, attrs);
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    public BindableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private static int getItemTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableListView.itemTemplate, 0);
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

    public List<?> getItemsSource() {
        if (adapter != null) {
            return adapter.getItemsSource();
        }
        return null;
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

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Object parameter = getItemsSource().get(position);
        if (onLongClick != null && onLongClick.canExecute(parameter)) {
            onLongClick.execute(parameter);
            return true;
        }
        return false;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object parameter = getItemsSource().get(position);
        if (onClick != null && onClick.canExecute(parameter)) {
            onClick.execute(parameter);
        }
    }

}
