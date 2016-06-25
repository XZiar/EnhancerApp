package xziar.enhancer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.util.Pair;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.util.NetworkUtil.NetCBHandler;
import xziar.enhancer.util.NetworkUtil.NetTask;

public class ImageUtil implements ComponentCallbacks2
{
	static final String LogTag = "ImageUtil";
	private static final Context context;
	private static File cacheDir;
	private static final ImageTasker mainTasker = new ImageTasker();
	private static LruCache<String, Drawable> memCache;
	private static final int maxPerSize, maxSize;

	static
	{
		context = BaseApplication.getContext();
		context.registerComponentCallbacks(new ImageUtil());
		maxSize = (int) (Runtime.getRuntime().maxMemory() / 4);
		maxPerSize = maxSize / 16;
		memCache = new LruCache<String, Drawable>(maxSize)
		{
			@Override
			protected int sizeOf(String key, Drawable value)
			{
				Bitmap bmp;
				if (value instanceof BitmapDrawable)
					bmp = ((BitmapDrawable) value).getBitmap();
				else if (value instanceof RoundedBitmapDrawable)
					bmp = ((RoundedBitmapDrawable) value).getBitmap();
				else
					throw new IllegalArgumentException("unsupported format: " + value.getClass());
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
			Bitmap bmp = img.getBitmap();
			if (bmp.getByteCount() > maxPerSize)
				Log.d(LogTag, md5 + " too large to save in cache: " + bmp.getRowBytes() + "*"
						+ bmp.getHeight());
			else
			{
				Log.d(LogTag, md5 + " done save to cache: " + bmp.getByteCount());
				memCache.put(md5, img);
			}
		}

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

	protected static void saveToDisk(String md5, InputStream ins) throws IOException
	{
		try
		{
			File f = new File(cacheDir, md5 + ".png");
			FileOutputStream fos = new FileOutputStream(f);
			ByteStreams.copy(ins, fos);
			ins.close();
			fos.close();
			Log.d(LogTag, "save to disk: " + md5);
			return;
		}
		catch (IOException e)
		{
			Log.w(LogTag, "error saving with filestream", e);
			throw e;
		}
	}

	protected static BitmapDrawable readFromCache(String md5)
	{
		BitmapDrawable img = (BitmapDrawable) memCache.get(md5);
		if (img == null)
			Log.v(LogTag, "miss on cache: " + md5);
		else
		{
			Log.d(LogTag, "load from cache: " + md5);
			Log.d("LRU", "count : " + memCache.hitCount() + "/" + memCache.missCount());
		}
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

	private static boolean leanReadFromDisk(String md5)
	{
		File f = new File(cacheDir, md5 + ".png");
		if (!f.exists())
		{
			Log.v(LogTag, "miss on disk: " + md5);
			return false;
		}
		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), opt);
		int preSize = opt.outWidth * opt.outHeight * 4;
		if (preSize > maxPerSize)
			return true;
		Log.d(LogTag, "load from disk(lean): " + md5);
		bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), opt);
		if (bmp == null)
			return false;
		if (!bmp.hasAlpha())
		{
			Log.d(LogTag, "reload for non-alpha one");
			opt.inPreferredConfig = Config.RGB_565;
			bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), opt);
		}
		BitmapDrawable img = new BitmapDrawable(context.getResources(), bmp);
		bmp = null;
		saveToCache(md5, img);
		return true;
	}

	protected static BitmapDrawable readFromDisk(String md5, int dWidth)
	{
		File f = new File(cacheDir, md5 + ".png");
		if (!f.exists())
		{
			Log.v(LogTag, "miss on disk: " + md5);
			return null;
		}
		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), opt);
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = Math.max(1, opt.outWidth / dWidth);
		Log.d(LogTag, "load from disk: " + md5 + " with sample " + opt.inSampleSize + ", "
				+ opt.outWidth + "/" + dWidth);
		bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), opt);
		if (!bmp.hasAlpha())
		{
			Log.d(LogTag, "reload for non-alpha one");
			opt.inPreferredConfig = Config.RGB_565;
			bmp = BitmapFactory.decodeFile(f.getAbsolutePath(), opt);
		}
		BitmapDrawable img = new BitmapDrawable(context.getResources(), bmp);
		bmp = null;
		saveToCache(md5, img);
		return img;
	}

	public static void leanCacheImage(String where, ImgHolder object)
	{
		if (where == null)
			return;
		if (readFromCache(object.md5) != null || leanReadFromDisk(object.md5) == true)
			return;

		mainTasker.downloadPic(object, where);
		return;
	}

	public static void loadImage(String where, ImgHolder object)
	{
		if (where == null)
		{
			object.setToHolder();
			return;
		}
		BitmapDrawable img;
		img = readFromCache(object.md5);
		if (img == null)
		{
			img = readFromDisk(object.md5, object.dWidth);
			if (img == null)
			{
				mainTasker.downloadPic(object, where);
				return;
			}
		}
		object.setDrawable(img);
	}

	public static RoundedBitmapDrawable getCircleDrawable(int resID)
	{
		RoundedBitmapDrawable img = (RoundedBitmapDrawable) memCache.get("" + resID);
		if (img != null)
			return img;
		// create new
		Bitmap src = BitmapFactory.decodeResource(context.getResources(), resID);
		int ds = src.getWidth() - src.getHeight();
		int size = Math.min(src.getWidth(), src.getHeight());
		if (ds != 0)// crop
		{
			src = Bitmap.createBitmap(src, ds > 0 ? ds / 2 : 0, ds > 0 ? 0 : ds / -2, size, size);
		}
		img = RoundedBitmapDrawableFactory.create(context.getResources(), src);
		img.setCornerRadius(size / 2);
		img.setAntiAlias(true);
		memCache.put(String.valueOf(resID), img);
		Log.d("CircleImageUtil",
				"cache drawable: " + context.getResources().getResourceEntryName(resID));
		return img;
	}

	public static Drawable tintDrawable(Drawable drawable, int color)
	{
		final Drawable newDrawable = DrawableCompat.wrap(drawable).mutate();
		DrawableCompat.setTint(newDrawable, color);
		return newDrawable;
	}

	public static interface OnLoadedCallback
	{
		public void callback(ImgHolder holder, Drawable img);
	}

	public static class ImgHolder
	{
		private static final String LogTag = "ImgHolder";
		public static final Drawable preImg = ContextCompat.getDrawable(context,
				R.drawable.icon_image_holder);
		private static final Drawable failImg = ContextCompat.getDrawable(context,
				R.drawable.icon_image_broken);
		private static final Drawable tipImg = ContextCompat.getDrawable(context,
				R.drawable.icon_setting);

		protected WeakReference<OnLoadedCallback> callback;

		protected int dWidth = 1000;
		public String md5, url;

		static
		{
			preImg.setBounds(0, 0, preImg.getIntrinsicWidth(), preImg.getIntrinsicHeight());
			failImg.setBounds(0, 0, failImg.getIntrinsicWidth(), failImg.getIntrinsicHeight());
			tipImg.setBounds(0, 0, tipImg.getIntrinsicWidth(), tipImg.getIntrinsicHeight());
		}

		public ImgHolder()
		{
			callback = new WeakReference<OnLoadedCallback>(null);
		}

		public void setToFail()
		{
			setDrawable(failImg);
		}

		public void setToHolder()
		{
			setDrawable(preImg);
		}

		public void setDrawable(Drawable img)
		{
			if (callback.get() != null)
			{
				callback.get().callback(this, img);
			}
		}

		public void setOnLoadedCallback(OnLoadedCallback callback)
		{
			this.callback = new WeakReference<OnLoadedCallback>(callback);
		}

	}

	public static class ImgViewHolder extends ImgHolder implements OnLoadedCallback
	{
		protected ImageView view;

		public ImgViewHolder(ImageView view)
		{
			super();
			this.view = view;
			setOnLoadedCallback(this);
		}

		public void getDrawable(ImgHolder obj)
		{
			md5 = obj.md5;
			dWidth = view.getWidth();
			if (dWidth == 0)
				dWidth = 1000;
			ImageUtil.loadImage(obj.url, this);
		}

		@Override
		public void callback(ImgHolder holder, Drawable img)
		{
			view.setImageDrawable(img);
		}
	}

	private static class ImgNetHandler extends NetCBHandler<Pair<ImgHolder, Drawable>>
	{
		public ImgNetHandler(NetTask<Pair<ImgHolder, Drawable>> callback)
		{
			super(callback);
		}

		@Override
		public void handleMessage(Message msg)
		{
			NetTask<Pair<ImgHolder, Drawable>> callback = ref.get();
			if (callback == null)
				return;
			callback.onDone(msg);// let onDone handle judgement
		}
	}

	private static class ImageTasker extends NetTask<Pair<ImgHolder, Drawable>>
	{
		BiMap<Call, ImgHolder> taskMap;
		private static RequestBody emptybody = new FormBody.Builder().build();

		public ImageTasker()
		{
			super(false);
			handler = new ImgNetHandler(this);
			taskMap = Maps.synchronizedBiMap(HashBiMap.<Call, ImgHolder> create());
		}

		/**
		 * load image from internet
		 * 
		 * @param holder
		 *            holder where to insert data into
		 * @param url
		 *            url where image exists
		 */
		public void downloadPic(ImgHolder holder, String url)
		{
			taskMap.inverse().remove(holder);// invalidate oldcall
			try
			{
				// Request request = new Request.Builder().url(url).post(emptybody).build();
				Request request = new Request.Builder().url(url).get().build();
				Log.v(LogTag, request.toString());
				Call call = run(request);
				taskMap.put(call, holder);
			}
			catch (IllegalArgumentException e)
			{
				holder.setToFail();
				onError(e);
			}
		}

		@Override
		protected Pair<ImgHolder, Drawable> parse(Call call, ResponseBody data)
				throws IOException, ParseResultFailException
		{
			// get mapping holder
			ImgHolder holder = taskMap.remove(call);
			if (holder == null)
				throw new ParseResultFailException("cancel");

			String md5 = holder.md5;
			int dW = holder.dWidth;
			saveToDisk(md5, data.byteStream());
			Drawable img = readFromDisk(md5, holder.dWidth);
			return new Pair<ImgHolder, Drawable>(holder, img);
		}

		@Override
		protected void onDoneBG(Call call, Message msg)
		{
			if (msg.what != RetCode.Success.ordinal())
			{
				ImgHolder holder = taskMap.get(call);
				if (holder == null)// cacelled
					msg.what = RetCode.Cancel.ordinal();
				msg.obj = new Object[] { msg.obj, holder };
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onDone(Message msg)
		{
			switch (RetCode.values()[msg.what])
			{
			case Success:
				onSuccess((Pair<ImgHolder, Drawable>) msg.obj);
				return;
			case Cancel:
				return;
			default:
				ImgHolder holder = (ImgHolder) ((Object[]) msg.obj)[1];
				holder.setToFail();
				return;
			}
		}

		@Override
		protected void onSuccess(Pair<ImgHolder, Drawable> data)
		{
			data.first.setDrawable(data.second);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	}

	@Override
	public void onLowMemory()
	{
	}

	@Override
	public void onTrimMemory(int level)
	{
		Log.w(LogTag, "need to trim memmory: level " + level);
		switch (level)
		{
		case TRIM_MEMORY_COMPLETE:
		case TRIM_MEMORY_BACKGROUND:
			memCache = new LruCache<String, Drawable>(maxSize);
			break;
		default:
			break;
		}

	};

}
