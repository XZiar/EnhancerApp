package xziar.enhancer.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
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

	@Inherited
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ObjView
	{
		public String value();
	}

	private static long elapse = 0;
	private static final String LogTag = "ViewInject";
	private static HashMap<Class<?>, Field> HolderMap = new HashMap<>();
	private static HashMap<Class<?>, Method> MethodMap = new HashMap<>();
	private static HashMap<Class<?>, HashMap<Field, Integer>> InjectMap = new HashMap<>();

	private static Method loadMeth(Class<?> clz) throws NoSuchMethodException
	{
		Method meth = MethodMap.get(clz);
		if (meth != null)
			return meth;
		// load method
		Log.d(LogTag, "load method: " + clz.getName());
		meth = clz.getMethod("findViewById", int.class);
		MethodMap.put(clz, meth);
		return meth;
	}

	private static HashMap<Field, Integer> loadField(Class<?> clz)
			throws NoSuchMethodException, NoSuchFieldException
	{
		HashMap<Field, Integer> ret = InjectMap.get(clz);
		if (ret != null)
			return ret;
		// load field
		Log.d(LogTag, "load field: " + clz.getName());
		ret = new HashMap<>();
		ObjView iv = clz.getAnnotation(ObjView.class);
		if (iv != null)
		{
			Log.d(LogTag, "load holder: " + iv.value());
			Field f = null;
			try
			{
				f = clz.getDeclaredField(iv.value());
				f.setAccessible(true);
			}
			catch (NoSuchFieldException e)
			{
				Log.w(LogTag, "ObjView not in this class");
				f = clz.getField(iv.value());
			}
			HolderMap.put(clz, f);
		}

		Field[] fields = clz.getDeclaredFields();
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
		long ctime = System.nanoTime();
		Log.d(LogTag, "inject: " + obj.toString());
		Class<?> clz = obj.getClass();
		try
		{
			HashMap<Field, Integer> injects = loadField(clz);
			Field f = HolderMap.get(clz);
			try
			{
				Object viewHolder = (f == null ? obj : f.get(obj));
				Class<?> holderClz = viewHolder.getClass();
				try
				{
					Method meth = loadMeth(holderClz);
					for (Map.Entry<Field, Integer> inj : injects.entrySet())
					{
						try
						{
							Object view = meth.invoke(viewHolder, inj.getValue());
							try
							{
								inj.getKey().set(obj, view);
							}
							catch (IllegalAccessException | IllegalArgumentException e)
							{
								Log.e(LogTag, "error when do inject\n" + inj.getKey().getName()
										+ " <== " + view, e);
							}
						}
						catch (InvocationTargetException e)
						{
							Log.e(LogTag, "error when findViewById on " + viewHolder + " ==> "
									+ inj.getValue(), e);
						}
					}
				}
				catch (NoSuchMethodException e)
				{
					Log.e(LogTag, "error when loadmeth: " + holderClz.getName(), e);
				}
			}
			catch (IllegalAccessException | IllegalArgumentException e)
			{
				Log.e(LogTag, "error when get viewHolder", e);
			}
		}
		catch (NoSuchFieldException | NoSuchMethodException e)
		{
			Log.e(LogTag, "error when loadfield", e);
		}

		ctime = System.nanoTime() - ctime;
		elapse += ctime;
		Log.i(LogTag, "excute time " + ctime / 1000000 + "ms, total " + elapse / 1000000 + "ms");
	}
}
