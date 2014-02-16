package com.alterego.androidbound.zzzztoremove;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
	private static final IContentProvider<Bitmap> bitmapProvider = new CachedProvider<Bitmap>(new HttpBitmapProvider());

	private String url;
	private ImageView imageView;

	public ImageAsyncTask(String url, ImageView imageView) {
		this.url = url;
		this.imageView = imageView;
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		Exceptional<Bitmap> bitmap = bitmapProvider.getContent(url);
		return bitmap.hasValue() ? bitmap.value() : null;
	}
	
	protected void onPostExecute(Bitmap bit) {
//		if(bit == null)
//			imageView.setBackgroundColor(Color.GRAY);
//		else
//			
		if(bit != null)
			imageView.setImageBitmap(bit);
	}
}