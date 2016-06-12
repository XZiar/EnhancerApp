package xziar.enhancer.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class ViewInject
{
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface BindView
	{
		public int value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ObjView
	{
		public String value();
	}

	private static final String LogTag = "ViewInject";
	private static HashMap<Class<?>, Field> HolderMap = new HashMap<>();
	private static HashMap<Class<?>, Method> MethodMap = new HashMap<>();
	private static HashMap<Class<?>, HashMap<Field, Integer>> InjectMap = new HashMap<>();

	private static Method loadMeth(Class<?> clz) throws NoSuchMethodException
	{
		Log.d(LogTag, "load method: " + clz.getName());
		Method meth = clz.getMethod("findViewById", int.class);
		MethodMap.put(clz, meth);
		return meth;
	}

	private static HashMap<Field, Integer> loadField(Class<?> clz)
			throws NoSuchFieldException, NoSuchMethodException
	{
		Log.d(LogTag, "load field: " + clz.getName());
		HashMap<Field, Integer> ret = new HashMap<>();
		Field[] fields = clz.getDeclaredFields();
		ObjView iv = clz.getAnnotation(ObjView.class);
		if (iv != null)
		{
			Log.d(LogTag, "load field: viewHolder object: " + iv.value());
			Field f = clz.getDeclaredField(iv.value());
			f.setAccessible(true);
			HolderMap.put(clz, f);
		}

		for (Field f : fields)
		{
			BindView b = f.getAnnotation(BindView.class);
			if (b != null)
			{
				f.setAccessible(true);
				ret.put(f, b.value());
			}
		}
		InjectMap.put(clz, ret);
		return ret;
	}

	public static void inject(Object obj)
	{
		Log.d(LogTag, "inject: " + obj.toString());
		Class<?> clz = obj.getClass();
		HashMap<Field, Integer> injects = InjectMap.get(clz);
		if (injects == null)
			try
			{
				injects = loadField(clz);
			}
			catch (NoSuchFieldException | NoSuchMethodException e)
			{
				Log.e(LogTag, "error when loadfield", e);
			}
		Field f = HolderMap.get(clz);
		Object viewHolder = null;
		try
		{
			viewHolder = (f == null ? obj : f.get(obj));
		}
		catch (IllegalAccessException | IllegalArgumentException e)
		{
			Log.e(LogTag, "error when get viewHolder", e);
		}
		Class<?> holderClz = viewHolder.getClass();
		Method meth = MethodMap.get(holderClz);
		if (meth == null)
			try
			{
				meth = loadMeth(holderClz);
			}
			catch (NoSuchMethodException e1)
			{
				Log.e(LogTag, "error when loadmeth", e1);
			}
		try
		{
			for (Map.Entry<Field, Integer> inj : injects.entrySet())
			{
				Object view = meth.invoke(viewHolder, inj.getValue());
				inj.getKey().set(obj, view);
			}
		}
		catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
		{
			Log.e(LogTag, "error when get inject", e);
		}
	}
}
