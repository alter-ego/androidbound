package com.alterego.androidbound.helpers;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class GroupInfo {

    @Getter private int mPosition;

    @Getter private int mIndex;

    public GroupInfo(int index, int position) {
        mIndex = index;
        mPosition = position;
    }
}
