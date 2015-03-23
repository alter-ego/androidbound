package solutions.alterego.androidbound.utils;

import java.util.ArrayList;

public class GroupingList<TKey, T> extends ArrayList<GroupedList<TKey, T>> {

    private static final long serialVersionUID = 4585190351916572431L;

    public GroupedList<TKey, T> get(TKey key) {
        for (GroupedList<TKey, T> grouped : this) {
            if (grouped.Key.equals(key)) {
                return grouped;
            }
        }
        return null;
    }

    public int indexOfKey(TKey key) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).Key.equals(key)) {
                return i;
            }
        }
        return -1;
    }
}
