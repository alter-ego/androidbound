package com.alterego.androidbound.helpers;

public class GroupInfo {
    private int position;
    private int index;

    public GroupInfo(int index, int position) {
        this.index = index;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public int getIndex() {
        return index;
    }
}
