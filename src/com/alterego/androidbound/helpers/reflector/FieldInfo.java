package com.alterego.androidbound.helpers.reflector;

import java.lang.reflect.Field;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix="m")
public class FieldInfo {

    @Getter private final Field mFieldOriginal;
    @Getter private final String mFieldName;
    @Getter private final Class<?> mFieldType;

    public FieldInfo(Field field) {
        mFieldOriginal = field;
        mFieldName = field.getName();
        mFieldType = field.getType();
    }


}
