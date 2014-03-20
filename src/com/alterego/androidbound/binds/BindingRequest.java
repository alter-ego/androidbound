package com.alterego.androidbound.binds;

import lombok.Data;

@Data
public class BindingRequest {
	private Object Source;
	private Object Target;
	private BindingSpecification Specification;
}
