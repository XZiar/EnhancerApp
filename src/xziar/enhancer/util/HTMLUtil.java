package xziar.enhancer.util;

import android.graphics.drawable.Drawable;
import android.os.Process;
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
		private HTMLwrapper wrapper;

		public ImgGetter(HTMLwrapper wrapper)
		{
			this.wrapper = wrapper;
		}

		void setDesireWidth(int width)
		{
			dWidth = width;
		}

		@Override
		public Drawable getDrawable(String source)
		{
			HolderDrawable holder = new HolderDrawable(dWidth);
			holder.setOnLoadedCallback(wrapper);
			return holder;
		}
	}

	private static class ImageLoader implements Runnable
	{
		private HTMLwrapper wrapper;

		public ImageLoader(HTMLwrapper wrapper)
		{
			this.wrapper = wrapper;
		}

		@Override
		public void run()
		{
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			ImageSpan[] imgs = wrapper.ss.getSpans(0, wrapper.ss.length(), ImageSpan.class);
			for (final ImageSpan img : imgs)
			{
				if (Thread.interrupted())
				{
					Log.d(LogTag, "re-launch load image");
					return;
				}
				HolderDrawable holder = (HolderDrawable) img.getDrawable();
				ImageUtil.loadImage(img.getSource(), holder);
			}
		}
	}

	public static class HTMLwrapper implements OnLoadedCallback, Runnable
	{
		private ImgGetter getter;
		private SpannableString ss;
		private TextView view;
		private Thread goLoadImg;
		private boolean isInRefresh = false;

		public HTMLwrapper(TextView view)
		{
			this.view = view;
			getter = new ImgGetter(this);
			goLoadImg = new Thread(new ImageLoader(this));
		}

		protected void refreshView(SpannableString nss)
		{
			view.setText(nss);
		};

		public void showHTML(String html)
		{
			getter.setDesireWidth(view.getWidth());
			ss = new SpannableString(Html.fromHtml(html, getter, null));
			if (goLoadImg.isAlive())
				goLoadImg.interrupt();
			goLoadImg.start();
			refreshView(ss);
		}

		@Override
		public void callback(HolderDrawable holder)
		{
			if (!isInRefresh)
			{
				isInRefresh = true;
				view.postDelayed(this, 500);
				Log.d(LogTag, "do refresh");
			}
			else
				Log.d(LogTag, "refresh too frequent");
		}

		@Override
		public void run()
		{
			isInRefresh = false;
			refreshView(ss);
		}
	}
}
