package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.VideoView;

public class BindableVideoView extends VideoView {
	private String source;

	public BindableVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BindableVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String value) {
		source = value;
		setVideoURI(Uri.parse(source));
		start();
	}
}
