package xziar.enhancer.util;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Process;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;
import xziar.enhancer.adapter.ImageAdapter;
import xziar.enhancer.util.ImageUtil.ImgHolder;

public class RichTextUtil
{
	private static final String LogTag = "RichTextUtil";

	public static class ImgGetter implements ImageGetter
	{
		@Override
		public Drawable getDrawable(String source)
		{
			return ImgHolder.preImg;
		}
	}

	public static class RTwrapper implements Runnable
	{
		private ImgGetter getter;
		private SpannableString ss;
		private TextView view;
		private ImageAdapter adapter;
		private ArrayList<ImgHolder> images = new ArrayList<>();
		private Thread goLoadImg;

		public RTwrapper(TextView view, ImageAdapter adapter)
		{
			this.view = view;
			this.adapter = adapter;
			getter = new ImgGetter();
			goLoadImg = new Thread(this);
		}

		protected void refreshView(SpannableString nss)
		{
			view.setText(nss);
		};

		public void showHTML(String html)
		{
			ss = new SpannableString(Html.fromHtml(html, getter, null));
			if (goLoadImg.isAlive())
				goLoadImg.interrupt();
			goLoadImg.start();
			refreshView(ss);
		}

		@Override
		public void run()
		{
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			ImageSpan[] imgs = ss.getSpans(0, ss.length(), ImageSpan.class);
			images.clear();
			for (final ImageSpan img : imgs)
			{
				if (Thread.interrupted())
				{
					Log.w(LogTag, "re-launch load image");
					return;
				}
				ImgHolder holder = new ImgHolder();
				String url = img.getSource();
				if (url != null)
				{
					holder.url = url;
					holder.md5 = MD5Util.md5(url.getBytes());
					ImageUtil.loadImage(url, holder);
				}
				images.add(holder);
			}
			imgs = null;
			adapter.refresh(images);
		}

		public void release()
		{
			ss = null;
			view.setText("");
			view = null;
			if (goLoadImg.isAlive())
				goLoadImg.interrupt();
			goLoadImg = null;
		}
	}
}
