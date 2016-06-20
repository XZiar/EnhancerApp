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
		public int value() default 0;

		public String onClick() default "";
	}

	@Inherited
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ObjView
	{
		public String value() default "";

		public String resource() default "";
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

	private static void loadHolder(Class<?> clz) throws NoSuchFieldException
	{
		ObjView ov = clz.getAnnotation(ObjView.class);
		if (ov == null)
			return;
		String holderName = ov.value();
		if (holderName == "")// default value
			return;
		Field holder;
		Log.d(LogTag, "load holder: " + ov.value());
		try
		{
			holder = clz.getDeclaredField(ov.value());
			holder.setAccessible(true);
		}
		catch (NoSuchFieldException e)
		{
			Log.w(LogTag, "ObjView not in this class");
			holder = clz.getField(ov.value());
		}
		HolderMap.put(clz, holder);
		return;
	}

	private static HashMap<Field, Integer> loadField(Class<?> clz)
			throws NoSuchMethodException, NoSuchFieldException
	{
		HashMap<Field, Integer> ret = InjectMap.get(clz);
		if (ret != null)
			return ret;
		// load holder
		try
		{
			loadHolder(clz);
		}
		catch (NoSuchFieldException e)
		{
			Log.e(LogTag, "error when load holder for " + clz.getName(), e);
		}
		// load field
		Log.d(LogTag, "load field: " + clz.getName());
		ret = new HashMap<>();

		Field[] fields = clz.getDeclaredFields();
		for (Field f : fields)
		{
			BindView b = f.getAnnotation(BindView.class);
			if (b != null)
			{
				f.setAccessible(true);
				int val = b.value();
				if (val != 0)
					ret.put(f, val);
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
			Field holder = HolderMap.get(clz);
			try
			{
				Object viewHolder = (holder == null ? obj : holder.get(obj));
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
