package xziar.enhancer.util;

import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import xziar.enhancer.activity.MainActivity;

public class CircleImageUtil
{
	private static HashMap<Integer, RoundedBitmapDrawable> cache = new HashMap<>();
	private static Resources res = MainActivity.getAppContext().getResources();

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
			src = Bitmap.createBitmap(src, ds > 0 ? ds / 2 : 0,
					ds > 0 ? 0 : ds / -2, size, size);
		}
		img = RoundedBitmapDrawableFactory.create(res, src);
		img.setCornerRadius(size / 2);
		img.setAntiAlias(true);
		cache.put(resID, img);
		Log.d("CircleImageUtil",
				"cache drawable: " + res.getResourceEntryName(resID));
		return img;
	}
}
