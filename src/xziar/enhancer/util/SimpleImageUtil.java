package xziar.enhancer.util;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.util.SparseArray;

public class SimpleImageUtil implements ComponentCallbacks2
{
	private static SparseArray<RoundedBitmapDrawable> cache = new SparseArray<>();
	private static Resources res = BaseApplication.getContext().getResources();

	static
	{
		BaseApplication.getContext().registerComponentCallbacks(new SimpleImageUtil());
	}

	public static RoundedBitmapDrawable getCircleDrawable(int resID)
	{
		RoundedBitmapDrawable img = cache.get(resID);
		if (img != null)
			return img;
		// create new
		Bitmap src = BitmapFactory.decodeResource(res, resID);
		int ds = src.getWidth() - src.getHeight();
		int size = Math.min(src.getWidth(), src.getHeight());
		if (ds != 0)// crop
		{
			src = Bitmap.createBitmap(src, ds > 0 ? ds / 2 : 0, ds > 0 ? 0 : ds / -2, size, size);
		}
		img = RoundedBitmapDrawableFactory.create(res, src);
		img.setCornerRadius(size / 2);
		img.setAntiAlias(true);
		cache.put(resID, img);
		Log.d("CircleImageUtil", "cache drawable: " + res.getResourceEntryName(resID));
		return img;
	}

	public static Drawable tintDrawable(Drawable drawable, int color)
	{
		final Drawable newDrawable = DrawableCompat.wrap(drawable).mutate();
		DrawableCompat.setTint(newDrawable, color);
		return newDrawable;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	}

	@Override
	public void onLowMemory()
	{
	}

	@Override
	public void onTrimMemory(int level)
	{
		cache.clear();
	}
}
