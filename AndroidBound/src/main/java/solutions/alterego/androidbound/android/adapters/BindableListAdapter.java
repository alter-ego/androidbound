package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.Map;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.interfaces.INeedsBoundView;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableListAdapter extends BaseAdapter {

    private static final int VIEW_LAYOUT_TAG = 1 + 2 << 24;

    private final Context context;

    private IViewBinder viewBinder;

    private int mItemTemplate;

    private Map<Class<?>, Integer> mTemplatesForObjects;

    private List<?> itemsSource;

    private ILogger mLogger;

    public BindableListAdapter(Context ctx, IViewBinder vb, int itemTemplate, List<?> items) {
        context = ctx;
        viewBinder = vb;
        mItemTemplate = itemTemplate;
        itemsSource = items;

        if (viewBinder == null) {
            if (context instanceof IBindableView) {
                viewBinder = ((IBindableView) context).getViewBinder();
            }
        }

        mLogger = viewBinder == null
                ? NullLogger.instance
                : viewBinder.getLogger();
    }

    public void setItemsSource(List<?> value) {
        itemsSource = value;
        notifyDataSetChanged();
    }

    public void setTemplatesForObjects(Map<Class<?>, Integer> templatesForObjects) {
        mTemplatesForObjects = templatesForObjects;

        if (itemsSource != null) {
            notifyDataSetChanged(); //TODO test this?
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
            convertView = inflateViewForObject(getItem(position), parent);
        }

        convertView = checkInflatedView(convertView, position, parent);

        if (convertView == null) {
            String msg = "BindableListAdapter getView is null, returning ViewStub!";
            mLogger.warning(msg);
            convertView = new ViewStub(context);
            if (viewBinder.isDebugMode()) {
                throw new RuntimeException(msg);
            }
        }
        return convertView;
    }

    private void bindTo(View convertView, Object object) {
        if (viewBinder == null) {
            return;
        }

        List<IBindingAssociationEngine> bindings = viewBinder.getViewBindingEngine().getBindingsForView(convertView);
        if (bindings == null || bindings.size() < 1) {
            mLogger.verbose("BindableListItemView bindTo bindings == null or 0");
            return;
        }

        mLogger.verbose("BindableListItemView bindTo continue with binding");
        for (IBindingAssociationEngine binding : bindings) {
            binding.setDataContext(object);
        }
    }

    private View inflateViewForObject(Object objectForLayout, ViewGroup parent) {
        int layoutToInflate = getLayoutTemplateForObject(objectForLayout);

        View inflatedView = viewBinder.inflate(context, objectForLayout, layoutToInflate, parent, false);
        inflatedView.setTag(VIEW_LAYOUT_TAG, layoutToInflate);
        if (objectForLayout instanceof INeedsBoundView) {
            ((INeedsBoundView) objectForLayout).setBoundView(inflatedView);
        }
        return inflatedView;
    }

    private View checkInflatedView(View inflatedView, int position, ViewGroup parent) {
        Object objectForLayout = getItem(position);

        //if the view has a tag and that tag corresponds to the layout ref for the object, we can just bind again
        if (inflatedView.getTag(VIEW_LAYOUT_TAG) != null
                && (int) inflatedView.getTag(VIEW_LAYOUT_TAG) == getLayoutTemplateForObject(objectForLayout)) {
            bindTo(inflatedView, objectForLayout);
        } else {
            //it was a different view layout, we need to inflate again
            inflatedView = inflateViewForObject(objectForLayout, parent);
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

    public int getItemTemplate() {
        return mItemTemplate;
    }

    public Map<Class<?>, Integer> getTemplatesForObjects() {
        return mTemplatesForObjects;
    }

    public List<?> getItemsSource() {
        return itemsSource;
    }
}
