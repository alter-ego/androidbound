package com.alterego.androidbound.binds;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m")
public class BindingRequest {

    private Object mSource;

    private Object mTarget;

    private BindingSpecification mSpecification;
}
