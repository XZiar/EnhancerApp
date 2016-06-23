package xziar.enhancer.util;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.util.NetworkUtil.NetCBHandler;
import xziar.enhancer.util.NetworkUtil.NetTask;

public class ImageUtil
{
	static final String LogTag = "ImageUtil";
	private static final Context context;
	static
	{
		context = BaseApplication.getContext();
	}

	public static class HolderDrawable extends Drawable
	{
		public interface OnLoadedCallback
		{
			public void callback(HolderDrawable holder);
		}

		private static final String LogTag = "HolderDrawable";
		private static final Drawable preImg = ContextCompat.getDrawable(context,
				R.drawable.icon_image_holder);
		private static final Drawable failImg = ContextCompat.getDrawable(context,
				R.drawable.icon_image_broken);

		protected Drawable obj = preImg;
		protected OnLoadedCallback callback;
		protected int dWidth = 0;

		static
		{
			preImg.setBounds(0, 0, preImg.getIntrinsicWidth(), preImg.getIntrinsicHeight());
			failImg.setBounds(0, 0, failImg.getIntrinsicWidth(), failImg.getIntrinsicHeight());
		}

		public HolderDrawable(int dWidth)
		{
			super();
			this.dWidth = dWidth;
			setBounds(preImg.getBounds());
		}

		public void setOnLoadedCallback(OnLoadedCallback callback)
		{
			this.callback = callback;
		}

		public void setToHolder()
		{
			setDrawable(preImg);
		}

		public void setToFail()
		{
			setDrawable(failImg);
		}

		public void setDrawable(Drawable img)
		{
			obj = (img == null ? preImg : img);
			if (obj.getBounds().width() <= 0)
			{
				int rw = obj.getIntrinsicWidth(), rh = obj.getIntrinsicHeight();
				if (dWidth == 0)
					obj.setBounds(0, 0, rw, rh);
				else
					obj.setBounds(0, 0, dWidth, rh * dWidth / rw);
			}
			setBounds(obj.getBounds());
			Log.v(LogTag, "set drawable. now bounds:" + getBounds().toShortString());
			if (callback != null)
				callback.callback(this);
		}

		@Override
		public void draw(Canvas canvas)
		{
			obj.draw(canvas);
		}

		@Override
		public int getIntrinsicWidth()
		{
			return getBounds().width();
		}

		@Override
		public int getIntrinsicHeight()
		{
			return getBounds().height();
		}

		@Override
		public void setAlpha(int alpha)
		{
		}

		@Override
		public void setColorFilter(ColorFilter colorFilter)
		{
		}

		@Override
		public int getOpacity()
		{
			return 0;
		}
	}

	public static void loadImage(String where, HolderDrawable object)
	{
		mainTasker.tryGetPic(object, where);
	}

	private static ImageTasker mainTasker = new ImageTasker();

	private static class ImgNetHandler extends NetCBHandler<Pair<HolderDrawable, Drawable>>
	{
		public ImgNetHandler(NetTask<Pair<HolderDrawable, Drawable>> callback)
		{
			super(callback);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg)
		{
			NetTask<Pair<HolderDrawable, Drawable>> callback = ref.get();
			if (callback == null)
				return;
			callback.onDone(msg);
			switch (NetTask.RetCode.values()[msg.what])
			{
			case Timeout:
				callback.onTimeout((Exception) ((Object[]) msg.obj)[0]);
				break;
			case Error:
			case Fail:
				callback.onError((Exception) ((Object[]) msg.obj)[0]);
				break;
			case Success:
				callback.onSuccess((Pair<HolderDrawable, Drawable>) msg.obj);
				break;
			case Unsuccess:
				callback.onUnsuccess(msg.arg1, (String) ((Object[]) msg.obj)[0]);
			}
		}
	}

	private static class ImageTasker extends NetTask<Pair<HolderDrawable, Drawable>>
	{
		ConcurrentHashMap<Call, HolderDrawable> taskMap = new ConcurrentHashMap<>();
		ConcurrentHashMap<HolderDrawable, Call> holderMap = new ConcurrentHashMap<>();
		private static RequestBody emptybody = new FormBody.Builder().build();

		public ImageTasker()
		{
			super(false);
			handler = new ImgNetHandler(this);
		}

		public void tryGetPic(HolderDrawable holder, String obj)
		{
			Call call = holderMap.get(holder);
			if (call != null)// invalidate oldcall
				taskMap.remove(call);
			try
			{
				Request request = new Request.Builder().url(obj).post(emptybody).build();
				call = run(request);
				holderMap.put(holder, call);
				taskMap.put(call, holder);
			}
			catch (IllegalArgumentException e)
			{
				onError(e);
			}
		}

		@Override
		protected Pair<HolderDrawable, Drawable> parse(Call call, ResponseBody data)
				throws IOException, ParseResultFailException
		{
			byte[] imgdata = data.bytes();
			Bitmap bmp = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);
			HolderDrawable holder = taskMap.get(call);
			if (holder == null)
				throw new ParseResultFailException("cancel");
			return new Pair<>(holder, (Drawable) new BitmapDrawable(bmp));
		}

		@Override
		protected void onDoneBG(Call call, Message msg)
		{
			HolderDrawable holder = taskMap.remove(call);
			if (holder != null)
				holderMap.remove(holder);
			if (msg.what != RetCode.Success.ordinal())
				msg.obj = new Object[] { msg.obj, holder };
		}

		@Override
		protected void onDone(Message msg)
		{
			if (msg.what != RetCode.Success.ordinal())
			{
				HolderDrawable holder = (HolderDrawable) ((Object[]) msg.obj)[1];
				holder.setToFail();
			}
		}

		@Override
		protected void onSuccess(Pair<HolderDrawable, Drawable> data)
		{
			data.first.setDrawable(data.second);
		}

	};

}
