package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.BindableListItemView;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableListAdapter extends BaseAdapter {

    private static final int VIEW_LAYOUT_TAG = 1 + 2 << 24;

    private final Context context;

    private IViewBinder viewBinder;

    @Getter
    private int mItemTemplate;

    @Getter
    private Map<Class<?>, Integer> mTemplatesForObjects;

    @Getter
    private List<?> itemsSource;

    public BindableListAdapter(Context ctx, IViewBinder vb, int itemTemplate) {
        context = ctx;
        viewBinder = vb;
        mItemTemplate = itemTemplate;

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

    public void setTemplatesForObjects(Map<Class<?>, Integer> templatesForObjects) {
        mTemplatesForObjects = templatesForObjects;

        if (itemsSource != null) {
            notifyDataSetInvalidated(); //TODO test this?
        }
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
            convertView = inflateViewForObject(getItem(position));
        } else if (convertView instanceof BindableListItemView) {
            convertView = checkInflatedView((BindableListItemView) convertView, position);
        } else {
            viewBinder.getLogger().info("BindableListAdapter getView not inflating, not rebinding");
        }

        if (convertView == null){
            viewBinder.getLogger().warning("BindableListAdapter getView is null, returning ViewStub!");
            convertView = new ViewStub(context);
        }

        return convertView;
    }

    private BindableListItemView inflateViewForObject(Object objectForLayout) {
        int layoutToInflate = getLayoutTemplateForObject(objectForLayout);

        BindableListItemView inflatedView = new BindableListItemView(context, viewBinder, layoutToInflate, objectForLayout);
        inflatedView.setTag(VIEW_LAYOUT_TAG, layoutToInflate);

        return inflatedView;
    }

    private BindableListItemView checkInflatedView(BindableListItemView inflatedView, int position) {
        Object objectForLayout = getItem(position);

        //if the view has a tag and that tag corresponds to the layout ref for the object, we can just bind again
        if (inflatedView.getTag(VIEW_LAYOUT_TAG) != null
                && (int) inflatedView.getTag(VIEW_LAYOUT_TAG) == getLayoutTemplateForObject(objectForLayout)) {
            inflatedView.bindTo(objectForLayout);
        } else {
            //it was a different view layout, we need to inflate again
            inflatedView = inflateViewForObject(objectForLayout);
        }

        return inflatedView;
    }

    private int getLayoutTemplateForObject(Object objectForLayout) {
        int layoutToInflate = mItemTemplate;

        //if we have separate templates for objects through Java code, they will override itemTemplate layout ref set from XML.
        //that way you can also use XML itemTemplate as default layout.
        if (mTemplatesForObjects != null && mTemplatesForObjects.containsKey(objectForLayout.getClass())) {
            layoutToInflate = mTemplatesForObjects.get(objectForLayout.getClass());
        }

        return layoutToInflate;
    }
}
