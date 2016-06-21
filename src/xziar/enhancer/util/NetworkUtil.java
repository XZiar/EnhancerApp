package xziar.enhancer.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import xziar.enhancer.R;
import xziar.enhancer.activity.MainActivity;

public class NetworkUtil
{
	static final String LogTag = "NetworkUtil";
	private static final Context context;
	private static final OkHttpClient client;
	private static final Proxy proxy;
	public static HttpUrl baseUrl;

	private static final CookieJar cookiejar = new CookieJar()
	{
		private final HashMap<String, List<Cookie>> cookieMap = new HashMap<>();

		@Override
		public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
		{
			cookieMap.put(url.host(), cookies);
			Log.d(LogTag, "new cookies from " + url.host() + "\n");
			for (Cookie ck : cookies)
				Log.d(LogTag, "\r " + ck.toString());
		}

		@Override
		public List<Cookie> loadForRequest(HttpUrl url)
		{
			List<Cookie> cookies = cookieMap.get(url.host());
			return cookies == null ? new ArrayList<Cookie>() : cookies;
		}
	};
	static
	{
		Builder builder = new OkHttpClient.Builder().cookieJar(cookiejar);
		context = MainActivity.getAppContext();
		InputStream ins = context.getResources().openRawResource(R.raw.network);
		JSONObject data = new JSONObject();
		try
		{
			byte[] dat = new byte[ins.available()];
			ins.read(dat, 0, ins.available());
			ins.close();
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
					new InetSocketAddress(JOproxy.getString("host"), JOproxy.getIntValue("port")));
			Log.d(LogTag, "proxy : " + proxy.toString());
			builder.proxy(proxy);
		}
		else
		{
			proxy = null;
		}
		client = builder.build();
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
			callback.onDone();
			switch (NetTask.RetCode.values()[msg.what])
			{
			case Timeout:
				callback.onTimeout();
				break;
			case Error:
			case Fail:
				callback.onError((Exception) msg.obj);
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
		public static class ParseResultFailException extends Exception
		{
			private static final long serialVersionUID = 5389772685857717961L;
			private final String msg;

			public ParseResultFailException(String msg)
			{
				super();
				this.msg = msg;
			}

			public String getMsg()
			{
				return msg;
			}

		}

		static enum RetCode
		{
			Timeout, Error, Fail, Success, Unsuccess
		}

		protected NetCBHandler<D> handler;
		protected String url;
		protected final boolean isSingleton;
		protected boolean isRunning;
		protected Object taskdata;

		public NetTask(String addr, boolean isSingleton)
		{
			handler = new NetCBHandler<D>(this);
			url = baseUrl + addr;
			this.isSingleton = isSingleton;
		}

		public NetTask(String addr)
		{
			this(addr, false);
		}

		/**
		 * set essential data of the task (without thread-safe gerantee)
		 * 
		 * @param taskdata
		 *            essential data of the task
		 * @return
		 * 		NetTask itself
		 */
		public final NetTask<D> withData(Object taskdata)
		{
			this.taskdata = taskdata;
			return this;
		}

		/**
		 * get essential data of the task
		 * 
		 * @return
		 */
		protected final Object getTaskdata()
		{
			return taskdata;
		}

		/**
		 * post action
		 * 
		 * @param form
		 *            a Map<String, ?> that contains form data
		 * @param isMultiPart
		 *            determine whether use multipart/form
		 */
		public final void post(Map<String, ? extends Object> form, boolean isMultiPart)
		{
			if (!isMultiPart)
			{
				post(form);
				return;
			}
			MultipartBody.Builder fbBuilder = new MultipartBody.Builder();
			fbBuilder.setType(MultipartBody.FORM);
			for (Map.Entry<String, ? extends Object> e : form.entrySet())
			{
				Object val = e.getValue();
				if (val == null)
					fbBuilder.addFormDataPart(e.getKey(), "");
				else if (val.getClass() == String.class)
					fbBuilder.addFormDataPart(e.getKey(), (String) val);
				else if (val.getClass() == byte[].class)
				{
					fbBuilder.addFormDataPart(e.getKey(), "tmp.png",
							RequestBody.create(MediaType.parse("image/png"), (byte[]) val));
				}
			}
			RequestBody formBody = fbBuilder.build();
			Request request = new Request.Builder().url(url).post(formBody).build();
			run(request);
		};

		/**
		 * post action
		 * 
		 * @param form
		 *            a Map<String, ?> that contains form data.
		 *            Reponse form will be create using ?'s toString
		 */
		public final void post(Map<String, ? extends Object> form)
		{
			FormBody.Builder fbBuilder = new FormBody.Builder();
			for (Map.Entry<String, ? extends Object> e : form.entrySet())
			{
				String val = (e.getValue() == null ? "" : e.getValue().toString());
				fbBuilder.add(e.getKey(), val);
			}
			RequestBody formBody = fbBuilder.build();
			Request request = new Request.Builder().url(url).post(formBody).build();
			run(request);
		}

		/**
		 * post action
		 * 
		 * @param args
		 *            argments to form the form, expected an even number of
		 *            argment
		 * @throws IllegalArgumentException
		 */
		public final void post(Object... args) throws IllegalArgumentException
		{
			if (args.length % 2 != 0)
				throw new IllegalArgumentException(
						String.format("receive %d argment, expected an even number", args.length));
			HashMap<String, Object> form = new HashMap<>();
			for (int a = 0; a < args.length; a += 2)
				form.put(args[a].toString(), args[a + 1]);
			post(form);
		}

		public void get()
		{
		}

		protected final void run(Request request)
		{
			if (isSingleton && isRunning)
				return;
			isRunning = true;
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
			isRunning = false;
		}

		@Override
		public final void onResponse(Call call, final Response response)
		{
			Message msg = Message.obtain(handler);
			try
			{
				ResponseBody data = response.body();
				if (response.isSuccessful())
				{
					msg.what = RetCode.Success.ordinal();
					msg.obj = parse(data);
				}
				else
				{
					msg.what = RetCode.Unsuccess.ordinal();
					msg.arg1 = response.code();
					msg.obj = data.string();
				}
			}
			catch (IOException e)
			{
				msg.what = RetCode.Fail.ordinal();
				msg.obj = e;
			}
			catch (ParseResultFailException e)
			{
				msg.what = RetCode.Unsuccess.ordinal();
				msg.arg1 = response.code();
				msg.obj = e.getMsg();
			}
			msg.sendToTarget();
			isRunning = false;
		};

		/**
		 * parser that translates respnse to demanding data
		 * 
		 * @param data
		 *            response body
		 * @return
		 * 		specific data demanded
		 * @throws IOException
		 *             exception that may be thrown when reading response body's
		 *             data
		 * @throws ParseResultFailException
		 *             exception that may be thrown if data parsed from response
		 *             body does not meets demand
		 */
		@SuppressWarnings("unchecked")
		protected D parse(ResponseBody data) throws IOException, ParseResultFailException
		{
			return (D) data.string();
		};

		/**
		 * work to be done after launch the network connection
		 * runs on MainThread
		 */
		protected void onStart()
		{
		};

		/**
		 * work to be done before invoke callback
		 * runs on MainThread
		 */
		protected void onDone()
		{
		};

		/**
		 * callback when connection timeout
		 */
		protected void onTimeout()
		{
			Log.w(LogTag, "HTTP timeout");
			onFail();
		};

		/**
		 * callback when connection throws exception
		 * 
		 * @param e
		 *            exception throwed by connection
		 */
		protected void onError(Exception e)
		{
			Log.w(LogTag, "HTTP fail", e);
			onFail();
		};

		/**
		 * callback when connection return unsuccess state code
		 * 
		 * @param code
		 *            response code
		 * @param data
		 *            response body
		 */
		protected void onUnsuccess(int code, String data)
		{
			Log.w(LogTag, "HTTP unsuccess: " + code);
			onFail();
		};

		/**
		 * callback when successfully get data
		 */
		protected void onSuccess(D data)
		{
		};

		/**
		 * callback when task is unsuccessfullly done
		 * (designed to run before other specific callback)
		 */
		protected void onFail()
		{
		};
	}

	public static class NetBeanTask<D> extends NetTask<D>
	{
		private final Class<D> clz;
		protected final String datname;
		protected Context context = MainActivity.getAppContext();

		public NetBeanTask(String addr, String obj, Class<D> clz)
		{
			super("/app" + addr, true);
			this.clz = clz;
			this.datname = obj;
		}

		public final void init(Context context)
		{
			this.context = context;
		}

		@Override
		protected D parse(ResponseBody data) throws IOException, ParseResultFailException
		{
			try
			{
				JSONObject obj = JSON.parseObject(data.string());
				if (obj.getBooleanValue("success"))
					return JSON.parseObject(obj.getString(datname), clz);
				else
					throw new ParseResultFailException(obj.getString("msg"));
			}
			catch (JSONException e)
			{
				Log.w(LogTag, "error when parse response to json", e);
				throw new ParseResultFailException("error syntax");
			}
		}

		@Override
		protected void onFail()
		{
			Toast.makeText(context, "ÍøÂç´íÎó", Toast.LENGTH_SHORT).show();
		}
	};

	public static class NetBeansTask<D> extends NetBeanTask<List<D>>
	{
		private final Class<D> baseclz;

		public NetBeansTask(String addr, String obj, Class<D> clz)
		{
			super(addr, obj, null);
			baseclz = clz;
		}

		@Override
		protected List<D> parse(ResponseBody data) throws IOException, ParseResultFailException
		{
			try
			{
				JSONObject obj = JSON.parseObject(data.string());
				if (obj.getBooleanValue("success"))
					return JSON.parseArray(obj.getString(datname), baseclz);
				else
					throw new ParseResultFailException(obj.getString("msg"));
			}
			catch (JSONException e)
			{
				Log.w(LogTag, "error when parse response to json array", e);
				throw new ParseResultFailException("error syntax");
			}
		}
	};
}
