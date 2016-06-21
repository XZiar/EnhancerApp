package xziar.enhancer.util;

import android.util.Log;

public class SizeUtil
{
	private static final String LogTag = "SizeUtil";
	private static float scale, fscale;
	static
	{
		try
		{
			scale = BaseApplication.getContext().getResources().getDisplayMetrics().density;
			fscale = BaseApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
			Log.d(LogTag, "SizeUtil initialize with scale=" + scale + ",fscale=" + fscale);
		}
		catch (Exception e)
		{
			Log.w(LogTag, "SizeUtil initialize fail");
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
