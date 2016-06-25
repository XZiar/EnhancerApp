package xziar.enhancer.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import xziar.enhancer.R;
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;
import xziar.enhancer.adapter.ImageAdapter;
import xziar.enhancer.util.ImageUtil.ImgHolder;
import xziar.enhancer.util.ImageUtil.ImgViewHolder;

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

	public static class ImgLoader implements Runnable
	{
		private RTwrapper wrapper;

		public ImgLoader(RTwrapper wrapper)
		{
			this.wrapper = wrapper;
		}

		@Override
		public void run()
		{
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			SpannableString ss = wrapper.ss;
			ImageSpan[] ispans = ss.getSpans(0, ss.length(), ImageSpan.class);
			wrapper.images.clear();
			for (ImageSpan ispan : ispans)
			{
				if (Thread.interrupted())
				{
					Log.w(LogTag, "re-launch load image");
					return;
				}
				ImgHolder holder = new ImgHolder();
				String url = ispan.getSource();
				if (url != null)
				{
					holder.url = url;
					holder.md5 = MD5Util.md5(url.getBytes());
					ImageUtil.leanCacheImage(url, holder);
				}
				final int idx = wrapper.images.size();
				wrapper.images.add(holder);
				ClickableSpan nspan = new ClickableSpan()
				{
					@Override
					public void onClick(View widget)
					{
						wrapper.adapter.performItemClick(idx);
					}
				};
				ss.setSpan(nspan, ss.getSpanStart(ispan), ss.getSpanEnd(ispan),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			ispans = null;
			wrapper.view.post(wrapper);
			wrapper.adapter.refresh(wrapper.images);
		}
	}

	public static class RTwrapper
			implements Runnable, OnItemClickListener<ImgHolder>, OnDismissListener, OnClickListener
	{
		private ImgGetter getter;
		private SpannableString ss;
		private TextView view;
		private ImageView pic;
		private ImageButton close;
		private ImageAdapter adapter;
		private ArrayList<ImgHolder> images = new ArrayList<>();
		private AlertDialog imgDlg;
		private ImgViewHolder holder;
		private Thread goLoadImg;

		public RTwrapper(Context context, TextView view, ImageAdapter adapter)
		{
			this.view = view;
			view.setMovementMethod(LinkMovementMethod.getInstance());
			this.adapter = adapter;
			adapter.setOnItemClickListener(this);
			getter = new ImgGetter();
			goLoadImg = new Thread(new ImgLoader(this));

			View detailView = LayoutInflater.from(context).inflate(R.layout.dialog_img_detail,
					null);
			pic = (ImageView) detailView.findViewById(R.id.pic);
			close = (ImageButton) detailView.findViewById(R.id.action_close);
			holder = new ImgViewHolder(pic);
			close.setOnClickListener(this);
			imgDlg = new AlertDialog.Builder(context).setView(detailView).create();
			imgDlg.setOnDismissListener(this);
			imgDlg.getWindow().setBackgroundDrawableResource(R.color.transparent);
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

		public void release()
		{
			if (goLoadImg.isAlive())
				goLoadImg.interrupt();
			ss = null;
			view.setText("");
			view = null;
			images.clear();
			adapter.refresh(images);
			adapter = null;
			goLoadImg = null;
		}

		@Override
		public void OnClick(ImgHolder data)
		{
			imgDlg.show();
			holder.getDrawable(data);
		}

		@Override
		public void onDismiss(DialogInterface dialog)
		{
			pic.setImageDrawable(ImgHolder.preImg);
		}

		@Override
		public void onClick(View v)
		{
			if (v == close)
				imgDlg.dismiss();
		}

		@Override
		public void run()
		{
			view.setText(ss);
			Log.d(LogTag, "refresh text");
		}
	}
}
