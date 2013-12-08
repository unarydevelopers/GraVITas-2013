package com.gravitas2013.app;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

public class BitmapLruCache extends LruCache<String, Bitmap> implements
		ImageCache {
	public static int getDefaultLruCacheSize(Context context) {
		/*
		 * final int maxMemory = (int) (Runtime.getRuntime().maxMemory() /
		 * 1024); final int cacheSize = maxMemory / 8;
		 * 
		 * return cacheSize;
		 */
		final DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		final int screenWidth = displayMetrics.widthPixels;
		final int screenHeight = displayMetrics.heightPixels;
		final int screenBytes = screenWidth * screenHeight * 4; // 4 bytes per
																// pixel

		return screenBytes * 3;
	}

	public BitmapLruCache(Context context) {
		this(getDefaultLruCacheSize(context),context);
	}

	public BitmapLruCache(int sizeInKiloBytes,Context context) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}
