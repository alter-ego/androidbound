package com.alterego.androidbound.helpers.reflector;

import java.lang.reflect.Field;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m")
public class FieldInfo {

    private final Field mFieldOriginal;

    private final String mFieldName;

    private final Class<?> mFieldType;

    public FieldInfo(Field field) {
        mFieldOriginal = field;
        mFieldName = field.getName();
        mFieldType = field.getType();
    }
}
