package solutions.alterego.androidbound.example;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewNormalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int listSize = 10;

    private Context mContext;

    private List<RecyclerViewWithObjectsActivityViewModel.ListViewItem> mListViewItems
            = new ArrayList<RecyclerViewWithObjectsActivityViewModel.ListViewItem>();

    public RecyclerViewNormalAdapter(@NonNull Context context) {
        mContext = context;

        for (int i = 0; i < listSize; i++) {
            mListViewItems.add(new RecyclerViewWithObjectsActivityViewModel.ListViewItem(Integer.toString(i)));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AvatarViewHolder(LayoutInflater.from(mContext), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((AvatarViewHolder) holder).onBindViewHolder(mListViewItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mListViewItems == null ? 0 : mListViewItems.size();
    }

    public class AvatarViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        TextView textView;

        public AvatarViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.activity_listview_listitem, parent, false));
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        public void onBindViewHolder(@NonNull RecyclerViewWithObjectsActivityViewModel.ListViewItem item) {
            textView.setText(item.getTitle());
        }
    }
}