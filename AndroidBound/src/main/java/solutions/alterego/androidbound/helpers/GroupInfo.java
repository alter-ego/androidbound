package solutions.alterego.androidbound.helpers;

public class GroupInfo {

    private int mPosition;

    private int mIndex;

    public GroupInfo(int index, int position) {
        mIndex = index;
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public int getIndex() {
        return mIndex;
    }
}
