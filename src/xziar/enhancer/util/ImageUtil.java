package xziar.enhancer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.io.ByteStreams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.LruCache;
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
	private static File cacheDir;
	private static final ImageTasker mainTasker = new ImageTasker();
	private static final LruCache<String, BitmapDrawable> memCache;
	private static final int maxPerSize;

	static
	{
		context = BaseApplication.getContext();
		int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
		maxPerSize = maxSize / 16;
		memCache = new LruCache<String, BitmapDrawable>(maxSize)
		{
			@Override
			protected int sizeOf(String key, BitmapDrawable value)
			{
				Bitmap bmp = value.getBitmap();
				return bmp.getByteCount();
			}

		};
		cacheDir = context.getExternalCacheDir();
		Log.d(LogTag, "ext cache dir: " + cacheDir.getAbsolutePath());
		if (cacheDir == null || !cacheDir.exists())
		{
			cacheDir = context.getCacheDir();
			Log.d(LogTag, "inner cache dir: " + cacheDir.getAbsolutePath());
		}
	}

	protected static void saveToCache(String md5, BitmapDrawable img)
	{
		if (img != null)
		{
			if (img.getBitmap().getByteCount() > maxPerSize)
				Log.d(LogTag, "too large to save in cache");
			else
				memCache.put(md5, img);
		}
		Log.d(LogTag, "save to cache: " + md5);
	}

	protected static void saveToDisk(String md5, Drawable img)
	{
		Bitmap bmp;
		if (img instanceof BitmapDrawable)
			bmp = ((BitmapDrawable) img).getBitmap();
		else
		{
			int w = img.getIntrinsicWidth(), h = img.getIntrinsicHeight();
			bmp = Bitmap.createBitmap(w, h, (img.getOpacity() == PixelFormat.OPAQUE
					? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888));
			Canvas canvas = new Canvas(bmp);
			img.setBounds(0, 0, w, h);
			img.draw(canvas);
		}
		try
		{
			OutputStream ops = new FileOutputStream(new File(cacheDir, md5 + ".png"));
			bmp.compress(CompressFormat.PNG, 100, ops);
			ops.close();
			Log.d(LogTag, "save to disk: " + md5);
		}
		catch (IOException e)
		{
			Log.w(LogTag, "error saving with filestream", e);
		}
	}

	protected static File saveToDisk(String md5, InputStream ins) throws IOException
	{
		try
		{
			File f = new File(cacheDir, md5 + ".png");
			FileOutputStream fos = new FileOutputStream(f);
			ByteStreams.copy(ins, fos);
			ins.close();
			fos.close();
			Log.d(LogTag, "save to disk: " + md5);
			return f;
		}
		catch (IOException e)
		{
			Log.w(LogTag, "error saving with filestream", e);
			throw e;
		}
	}

	protected static BitmapDrawable readFromCache(String md5)
	{
		BitmapDrawable img = memCache.get(md5);
		if (img == null)
			Log.v(LogTag, "miss on cache: " + md5);
		else
			Log.d(LogTag, "load from cache: " + md5);
		Log.w("LRU", "count : " + memCache.hitCount() + "/" + memCache.missCount());
		return img;
	}

	protected static BitmapDrawable readFromDisk(String md5)
	{
		File f = new File(cacheDir, md5 + ".png");
		if (!f.exists())
		{
			Log.v(LogTag, "miss on disk: " + md5);
			return null;
		}
		try
		{
			InputStream ins = new FileInputStream(f);
			BitmapDrawable img = new BitmapDrawable(context.getResources(), ins);
			ins.close();
			Log.d(LogTag, "load from disk: " + md5);
			return img;
		}
		catch (IOException e)
		{
			Log.w(LogTag, "error with filestream", e);
		}
		return null;
	}

	public static void loadImage(String where, HolderDrawable object)
	{
		if (where == null)
		{
			object.setToHolder();
			return;
		}
		String md5 = MD5Util.md5(where.getBytes());
		BitmapDrawable img;
		img = readFromCache(md5);
		if (img == null)
		{
			img = readFromDisk(md5);
			if (img != null)
				saveToCache(md5, img);
			else
			{
				mainTasker.downloadPic(object, where, md5);
				return;
			}
		}
		object.setDrawable(img);
	}

	public static class HolderDrawable extends Drawable
	{
		public interface OnLoadedCallback
		{
			public void callback(HolderDrawable holder);
		}

		private static final String LogTag = "HolderDrawable";
		public static final Drawable preImg = ContextCompat.getDrawable(context,
				R.drawable.icon_image_holder);
		private static final Drawable failImg = ContextCompat.getDrawable(context,
				R.drawable.icon_image_broken);

		protected Drawable obj = preImg;
		protected WeakReference<OnLoadedCallback> callback;
		protected int dWidth = 0;
		protected String md5;

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
			this.callback = new WeakReference<OnLoadedCallback>(callback);
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
			OnLoadedCallback cb = callback.get();
			if (cb != null)
				cb.callback(this);
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

	private static class ImgNetHandler extends NetCBHandler<Object>
	{
		public ImgNetHandler(NetTask<Object> callback)
		{
			super(callback);
		}

		@Override
		public void handleMessage(Message msg)
		{
			NetTask<Object> callback = ref.get();
			if (callback == null)
				return;
			callback.onDone(msg);
			switch (NetTask.RetCode.values()[msg.what])
			{
			case Timeout:
				callback.onTimeout((Exception) msg.obj);
				break;
			case Error:
			case Fail:
				callback.onError((Exception) msg.obj);
				break;
			case Success:
				callback.onSuccess(msg.obj);
				break;
			case Unsuccess:
				callback.onUnsuccess(msg.arg1, (String) msg.obj);
			}
		}
	}

	private static class ImageTasker extends NetTask<Object>
	{
		ConcurrentHashMap<Call, HolderDrawable> callMap = new ConcurrentHashMap<>();
		ConcurrentHashMap<HolderDrawable, Call> holderMap = new ConcurrentHashMap<>();
		private static RequestBody emptybody = new FormBody.Builder().build();

		public ImageTasker()
		{
			super(false);
			handler = new ImgNetHandler(this);
		}

		/**
		 * load image from internet
		 * 
		 * @param holder
		 *            holder where to insert data into
		 * @param url
		 *            url where image exists
		 */
		public void downloadPic(HolderDrawable holder, String url, String md5)
		{
			Call call = holderMap.get(holder);
			if (call != null)// invalidate oldcall
				callMap.remove(call);
			holder.md5 = md5;
			try
			{
				// Request request = new Request.Builder().url(url).post(emptybody).build();
				Request request = new Request.Builder().url(url).get().build();
				Log.v(LogTag, request.toString());
				call = run(request);
				holderMap.put(holder, call);
				callMap.put(call, holder);
			}
			catch (IllegalArgumentException e)
			{
				holder.setToFail();
				onError(e);
			}
		}

		@Override
		protected Object parse(Call call, ResponseBody data)
				throws IOException, ParseResultFailException
		{
			// clean mapping
			HolderDrawable holder = callMap.remove(call);
			if (holder == null)
				throw new ParseResultFailException("cancel");
			holderMap.remove(holder);

			String md5 = holder.md5;
			int dW = holder.dWidth;
			File file = saveToDisk(md5, data.byteStream());
			Options opt = new Options();
			opt.inJustDecodeBounds = true;
			Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
			opt.inJustDecodeBounds = false;
			opt.inSampleSize = Math.max(1, opt.outWidth / dW);
			bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
			BitmapDrawable img = new BitmapDrawable(context.getResources(), bmp);
			bmp = null;
			saveToCache(md5, img);
			holder.setDrawable(img);

			holder = null;
			img = null;
			return true;
		}

		@Override
		protected void onDoneBG(Call call, Message msg)
		{
			HolderDrawable holder = callMap.remove(call);
			if (holder != null)// must not be successful
			{
				holderMap.remove(holder);
				holder.setToFail();
			}
		}

	};

}
