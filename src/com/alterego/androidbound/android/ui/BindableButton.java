package com.alterego.androidbound.android.ui;

import com.alterego.androidbound.interfaces.ICommand;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

public class BindableButton extends Button implements OnClickListener {
	private ICommand onClick = ICommand.empty;

	public BindableButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	public BindableButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnClickListener(this);
	}

	public ICommand getClick() {
		return onClick;
	}
	
	public void setClick(ICommand value) {
		if(value == null) {
			onClick = ICommand.empty;
			return;
		}
		onClick = value;
	}
	
	@Override
	public void onClick(View v) {
		if(onClick.canExecute(null))
			onClick.execute(null);
	}
}
