package xziar.enhancer.util;

import android.util.Log;
import xziar.enhancer.activity.MainActivity;

public class SizeUtil
{
	private static float scale;
	static
	{
		try
		{
			scale = MainActivity.getAppContext().getResources()
					.getDisplayMetrics().density;
			Log.v("tester", "SizeUtil initialize with scale=" + scale);
		}
		catch (Exception e)
		{
			Log.v("tester", "SizeUtil initialize fail");
			scale = 1.0f;
		}

	}

	public static int dp2px(int dp)
	{
		return (int) (dp * scale + 0.5f);
	}

	public static int px2dp(int px)
	{
		return (int) (px / scale + 0.5f);
	}
}
