package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.BindableListItemView;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableListAdapter extends BaseAdapter {

    private final Context context;

    private IViewBinder viewBinder;

    @Getter
    @Setter
    private int itemTemplate;

    @Getter
    private List<?> itemsSource;

    public BindableListAdapter(Context ctx, IViewBinder vb) {
        context = ctx;

        viewBinder = vb;

        if (viewBinder == null) {
            if (context instanceof IBindableView) {
                viewBinder = ((IBindableView) context).getViewBinder();
            }
        }
    }

    public void setItemsSource(List<?> value) {
        itemsSource = value;
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return itemsSource != null ? itemsSource.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return (itemsSource != null && itemsSource.size() > position) ? itemsSource.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new BindableListItemView(context, viewBinder, itemTemplate, getItem(position));
        } else if (convertView instanceof BindableListItemView) {
            ((BindableListItemView) convertView).bindTo(getItem(position));
        } else {
            ViewBinder.getLogger().info("BindableListAdapter getView not inflating, not rebinding");
        }

        return convertView;
    }
}
