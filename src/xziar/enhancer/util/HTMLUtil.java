package xziar.enhancer.util;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;
import xziar.enhancer.util.ImageUtil.HolderDrawable;
import xziar.enhancer.util.ImageUtil.HolderDrawable.OnLoadedCallback;

public class HTMLUtil
{
	private static final String LogTag = "HTMLUtil";

	public static class ImgGetter implements ImageGetter
	{
		private int dWidth = 0;

		void setDesireWidth(int width)
		{
			dWidth = width;
		}

		@Override
		public Drawable getDrawable(String source)
		{
			return new HolderDrawable(dWidth);
		}
	}

	public static class HTMLwrapper implements OnLoadedCallback
	{
		private ImgGetter getter;
		private SpannableString ss;
		private TextView view;

		public HTMLwrapper(TextView view)
		{
			this.view = view;
			getter = new ImgGetter();
		}

		protected void refreshView(SpannableString nss)
		{
			view.setText(nss);
		};

		public void showHTML(String html)
		{
			getter.setDesireWidth(view.getWidth());
			ss = new SpannableString(Html.fromHtml(html, getter, null));
			ImageSpan[] imgs = ss.getSpans(0, ss.length(), ImageSpan.class);
			for (final ImageSpan img : imgs)
			{
				Log.v(LogTag, "imgspan:" + img.getSource() + " ==> " + img.getDrawable());
				HolderDrawable holder = (HolderDrawable) img.getDrawable();
				holder.setOnLoadedCallback(this);
				ImageUtil.loadImage(img.getSource(), holder);
				// new Handler().postDelayed(new Runnable()
				// {
				// @Override
				// public void run()
				// {
				// Drawable d = ContextCompat.getDrawable(BaseApplication.getContext(),
				// R.drawable.slogon);
				// ((HolderDrawable) img.getDrawable()).setDrawable(d);
				// }
				// }, 2000);
			}
			refreshView(ss);
		}

		@Override
		public void callback(HolderDrawable holder)
		{
			refreshView(ss);
		}
	}
}
