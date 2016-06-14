package xziar.enhancer.util;

import android.util.Log;
import xziar.enhancer.activity.MainActivity;

public class SizeUtil
{
	private static float scale, fscale;
	static
	{
		try
		{
			scale = MainActivity.getAppContext().getResources()
					.getDisplayMetrics().density;
			fscale = MainActivity.getAppContext().getResources()
					.getDisplayMetrics().scaledDensity;
			Log.d("SizeUtil", "SizeUtil initialize with scale=" + scale
					+ ",fscale=" + fscale);
		}
		catch (Exception e)
		{
			Log.d("tester", "SizeUtil initialize fail");
			scale = fscale = 1.0f;
		}

	}

	public static int dp2px(float dp)
	{
		return (int) (dp * scale + 0.5f);
	}

	public static float px2dp(int px)
	{
		return px / scale;
	}

	public static int sp2px(float sp)
	{
		return (int) (sp * fscale + 0.5f);
	}

	public static float px2sp(int px)
	{
		return px / fscale;
	}
}
