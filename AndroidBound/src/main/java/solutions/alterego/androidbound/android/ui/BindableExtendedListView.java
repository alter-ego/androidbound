
package solutions.alterego.androidbound.android.ui;

import solutions.alterego.androidbound.android.adapters.BindableExtendedListAdapter;
import solutions.alterego.androidbound.interfaces.IBindableView;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IViewBinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

public class BindableExtendedListView extends ListView implements OnItemClickListener, OnItemLongClickListener, IBindableView {

    private AttributeSet mAttributes;
    private Context mContext;

    private ICommand onClick = ICommand.empty;
    private ICommand onLongClick = ICommand.empty;
    private BindableExtendedListAdapter adapter;
    private IViewBinder viewBinder;
    private HashMap<Class<?>, Integer> mTemplatesForObjects;

    public BindableExtendedListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        mAttributes = attrs;

        mTemplatesForObjects = new HashMap<Class<?>, Integer>();

        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    public BindableExtendedListView(Context context, AttributeSet attrs, int defStyle) {
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
            adapter = new BindableExtendedListAdapter(getContext(), getViewBinder());
            adapter.setTemplatesForObjects(mTemplatesForObjects);
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

    public HashMap<Class<?>, Integer> getTemplatesForObjects() {
        return mTemplatesForObjects;
    }

    public void setTemplatesForObjects(HashMap<Class<?>, Integer> map) {
        mTemplatesForObjects = map;
        if (adapter != null) {
            adapter.setTemplatesForObjects(mTemplatesForObjects);
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
