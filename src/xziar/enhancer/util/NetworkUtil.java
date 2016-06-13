package xziar.enhancer.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xziar.enhancer.R;
import xziar.enhancer.activity.MainActivity;

public class NetworkUtil
{
	static final String LogTag;
	private static final Context context;
	private static final OkHttpClient client;
	private static final Proxy proxy;
	public static HttpUrl baseUrl;

	static
	{
		LogTag = "NetworkUtil";
		context = MainActivity.getAppContext();

		InputStream ins = context.getResources().openRawResource(R.raw.network);

		JSONObject data = new JSONObject();
		try
		{
			byte[] dat = new byte[ins.available()];
			ins.read(dat, 0, ins.available());
			data = JSON.parseObject(new String(dat, "UTF-8"));
		}
		catch (IOException e)
		{
			Log.e(LogTag, "error when open file", e);
		}

		baseUrl = new HttpUrl.Builder().scheme(data.getString("scheme"))
				.host(data.getString("host")).port(data.getIntValue("port"))
				.addPathSegment(data.getString("base")).build();
		// url = new HttpUrl.Builder().scheme("http").host("120.27.106.188")
		// .port(8088).addPathSegment("RealTasker").build();
		Log.d(LogTag, "baseUrl : " + baseUrl.toString());

		JSONObject JOproxy = data.getJSONObject("proxy");
		if (JOproxy != null)
		{
			proxy = new Proxy(Proxy.Type.valueOf(JOproxy.getString("type")),
					new InetSocketAddress(JOproxy.getString("host"),
							JOproxy.getIntValue("port")));
			Log.d(LogTag, "proxy : " + proxy.toString());
			client = new OkHttpClient.Builder().proxy(proxy).build();
		}
		else
		{
			proxy = null;
			client = new OkHttpClient();
		}
	}

	public static boolean Test()
	{
		RequestBody formBody = new FormBody.Builder().add("un", "student")
				.add("pwd", "student").build();
		Request request = new Request.Builder().url(baseUrl + "/login")
				.post(formBody).build();
		client.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				Log.e(LogTag, "Test Failure", e);
			}

			@Override
			public void onResponse(Call call, Response response)
					throws IOException
			{
				Log.d(LogTag, "Test Response\n" + response.body().string());
			}
		});
		return true;
	}

	public static boolean Test(Callback callback)
	{
		RequestBody formBody = new FormBody.Builder().add("un", "student")
				.add("pwd", "student").build();
		Request request = new Request.Builder().url(baseUrl + "/login")
				.post(formBody).build();
		client.newCall(request).enqueue(callback);
		return true;
	}

	public static class CallBacker implements Callback
	{
		public static class UIHandler extends Handler
		{
			private WeakReference<CallBacker> ref;

			public UIHandler(CallBacker cb)
			{
				super(Looper.getMainLooper());
				ref = new WeakReference<CallBacker>(cb);
			}

			@Override
			public void handleMessage(Message msg)
			{
				CallBacker cb = ref.get();
				switch (RetCode.values()[msg.what])
				{
				case Timeout:
					cb.onTimeout();
					break;
				case Fail:
					cb.onFail((Exception) msg.obj);
					break;
				case Success:
					cb.onSuccess((String) msg.obj);
					break;
				}
			}
		}

		static enum RetCode
		{
			Timeout, Fail, Success,
		}

		protected UIHandler handler;

		public CallBacker()
		{
			handler = new UIHandler(this);
		}

		@Override
		public final void onFailure(Call call, final IOException e)
		{
			Message msg = Message.obtain(handler);
			Class<?> clz = e.getClass();
			if (clz == SocketTimeoutException.class)
			{
				msg.what = RetCode.Timeout.ordinal();
				handler.sendMessage(msg);
			}
			else
			{
				msg.what = RetCode.Fail.ordinal();
				msg.obj = e;
				handler.sendMessage(msg);
			}
		}

		protected void onTimeout()
		{
			Log.e(LogTag, "HTTP timeout");
		};

		protected void onFail(Exception e)
		{
			Log.e(LogTag, "HTTP fail", e);
		};

		protected void onSuccess(String data)
		{
			Log.d(LogTag, "response data");
		};

		@Override
		public final void onResponse(Call call, final Response response)
		{
			Message msg = Message.obtain(handler);
			try
			{
				msg.obj = response.body().string();
				msg.what = RetCode.Success.ordinal();
				handler.sendMessage(msg);
			}
			catch (IOException e)
			{
				msg.obj = e;
				msg.what = RetCode.Fail.ordinal();
				handler.sendMessage(msg);
			}

		};
	}
}
