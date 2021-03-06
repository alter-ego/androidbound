package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import java.util.List;

import io.reactivex.Observable;
import solutions.alterego.androidbound.android.adapters.BindableListAdapter;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableGridView extends GridView implements OnItemClickListener, OnItemLongClickListener, IBindableView, INotifyPropertyChanged {

    private int itemTemplate;

    protected BindableViewDelegate mDelegate;

    private BindableListAdapter mAdapter;

    private IViewBinder viewBinder;

    public BindableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupListView(attrs);
    }

    public BindableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupListView(attrs);
    }

    private void setupListView(AttributeSet attrs) {
        mDelegate = createDelegate(this);

        itemTemplate = getItemTemplate(attrs);

        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
    }

    private static int getItemTemplate(AttributeSet attrs) {
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

    /****** beginning of the delegated methods, to be copy/pasted in every bindable view ******/

    protected BindableViewDelegate createDelegate(View view) {
        return new BindableViewDelegate(view);
    }

    public ICommand getClick() {
        return mDelegate.getClick();
    }

    public void setClick(ICommand value) {
        mDelegate.setClick(value);
    }

    public ICommand getLongClick() {
        return mDelegate.getLongClick();
    }

    public void setLongClick(ICommand value) {
        mDelegate.setLongClick(value);
    }

    public int getBackgroundColor() {
        return mDelegate.getBackgroundColor();
    }

    public void setBackgroundColor(int color) {
        mDelegate.setBackgroundColor(color);
        super.setBackgroundColor(color);
    }

    public StateListDrawable getBackgroundDrawableState() {
        return mDelegate.getBackgroundDrawableState();
    }

    public void setBackgroundDrawableState(StateListDrawable colors) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.setBackground(colors);
            mDelegate.setBackgroundDrawableState(colors);
        }
    }

    public int getBackgroundResource() {
        return 0;
    }

    public void setBackgroundResource(int res) {
        super.setBackgroundResource(res);
    }

    public int getBackgroundDrawable() {
        return 0;
    }

    public void setBackgroundDrawable(Drawable res) {
        super.setBackgroundDrawable(res);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDelegate.onSizeChanged(w, h, oldw, oldh);
    }

    public void setWidth(int width) {
        mDelegate.setWidth(width);
    }

    public void setHeight(int height) {
        mDelegate.setHeight(height);
    }

    @Override
    public Observable<String> onPropertyChanged() {
        return mDelegate.onPropertyChanged();
    }

    @Override
    public void dispose() {
        mDelegate.dispose();
        setOnItemClickListener(null);
        setOnItemLongClickListener(null);

        mAdapter = null;
        viewBinder = null;
    }

    /****** end of the delegated methods, to be copy/pasted in every bindable view ******/

    public List<?> getItemsSource() {
        if (mAdapter != null) {
            return mAdapter.getItemsSource();
        }
        return null;
    }

    public void setItemsSource(List<?> value) {
        if (mAdapter == null) {
            mAdapter = new BindableListAdapter(getContext(), getViewBinder(), itemTemplate, value);
            setAdapter(mAdapter);
        } else {
            mAdapter.setItemsSource(value);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Object parameter = getItemsSource().get(position);
            if (!mDelegate.isDisposed() && mDelegate.getLongClick().canExecute(parameter)) {
                mDelegate.getLongClick().execute(parameter);
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Object parameter = getItemsSource().get(position);
            if (!mDelegate.isDisposed() && mDelegate.getClick().canExecute(parameter)) {
                mDelegate.getClick().execute(parameter);
            }
        } catch (Exception e) {
        }
    }

}
