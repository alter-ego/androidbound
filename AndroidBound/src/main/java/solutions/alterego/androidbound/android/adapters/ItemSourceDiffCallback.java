package solutions.alterego.androidbound.android.adapters;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class ItemSourceDiffCallback extends DiffUtil.Callback {

    private List<?> mOldList;

    private List<?> mNewList;

    ItemSourceDiffCallback(List<?> oldList, List<?> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList == null ? 0 : mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList == null ? 0 : mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return !((mOldList == null) || (mNewList == null)) && mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return !(mOldList == null || mNewList == null) && mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }
}
