package xziar.enhancer.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.Map;

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
	static final String LogTag = "NetworkUtil";
	private static final Context context;
	private static final OkHttpClient client;
	private static final Proxy proxy;
	public static HttpUrl baseUrl;

	static
	{
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
		Log.d(LogTag, "baseUrl : " + baseUrl);

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

	public static void Test(Callback callback)
	{
		RequestBody formBody = new FormBody.Builder().add("un", "student")
				.add("pwd", "student").build();
		Request request = new Request.Builder().url(baseUrl + "/login")
				.post(formBody).build();
		client.newCall(request).enqueue(callback);
	}

	public static class NetCBHandler<D> extends Handler
	{
		private WeakReference<NetTask<D>> ref;

		public NetCBHandler(NetTask<D> callback)
		{
			super(Looper.getMainLooper());
			ref = new WeakReference<>(callback);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg)
		{
			NetTask<D> callback = ref.get();
			if (callback == null)
				return;
			switch (NetTask.RetCode.values()[msg.what])
			{
			case Timeout:
				callback.onTimeout();
				break;
			case Error:
			case Fail:
				callback.onFail((Exception) msg.obj);
				break;
			case Success:
				callback.onSuccess((D) msg.obj);
				break;
			case Unsuccess:
				callback.onUnsuccess(msg.arg1, (String) msg.obj);
			}
		}
	}

	public static class NetTask<D> implements Callback
	{
		static enum RetCode
		{
			Timeout, Error, Fail, Success, Unsuccess
		}

		protected NetCBHandler<D> handler;
		protected String url;

		public NetTask(String addr)
		{
			handler = new NetCBHandler<D>(this);
			url = baseUrl + addr;
		}

		public void post(Map<String, String> form)
		{
			FormBody.Builder fbBuilder = new FormBody.Builder();
			for (Map.Entry<String, String> e : form.entrySet())
			{
				String val = (e.getValue() == null ? "" : e.getValue());
				fbBuilder.add(e.getKey(), val);
			}
			RequestBody formBody = fbBuilder.build();
			Request request = new Request.Builder().url(url).post(formBody)
					.build();
			run(request);
		}

		public void get()
		{
		}

		protected final void run(Request request)
		{
			client.newCall(request).enqueue(this);
			onStart();
		}

		@Override
		public final void onFailure(Call call, final IOException e)
		{
			Message msg = Message.obtain(handler);
			Class<?> clz = e.getClass();
			if (clz == SocketTimeoutException.class)
			{
				msg.what = RetCode.Timeout.ordinal();
			}
			else
			{
				msg.what = RetCode.Error.ordinal();
				msg.obj = e;
			}
			msg.sendToTarget();
		}

		@Override
		public final void onResponse(Call call, final Response response)
		{
			Message msg = Message.obtain(handler);
			try
			{
				String data = response.body().string();
				if (response.isSuccessful())
				{
					msg.what = RetCode.Success.ordinal();
					msg.obj = parse(data);
				}
				else
				{
					msg.what = RetCode.Unsuccess.ordinal();
					msg.arg1 = response.code();
					msg.obj = data;
				}
			}
			catch (IOException e)
			{
				msg.obj = e;
				msg.what = RetCode.Fail.ordinal();
			}
			msg.sendToTarget();
		};

		@SuppressWarnings("unchecked")
		protected D parse(String data)
		{
			return (D) data;
		};

		protected void onStart()
		{
		};

		protected void onTimeout()
		{
			Log.e(LogTag, "HTTP timeout");
		};

		protected void onFail(Exception e)
		{
			Log.e(LogTag, "HTTP fail", e);
		};

		protected void onSuccess(D data)
		{
		};

		protected void onUnsuccess(int code, String data)
		{
		};
	}
}
