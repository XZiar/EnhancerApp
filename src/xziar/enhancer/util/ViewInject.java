package xziar.enhancer.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.Resources;
import android.util.Log;
import android.view.View.OnClickListener;

public class ViewInject
{
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface BindView
	{
		public int value()

		default 0;

		public String onClick() default "";
	}

	@Inherited
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ObjView
	{
		public String value() default "";
	}

	private static class InjectData
	{
		static class Element
		{
			static enum BindState
			{
				none, self, other
			}

			final Field obj;
			final int id;
			BindState bindClick = BindState.none;
			Method setOnClick;

			public Element(Field obj, int id)
			{
				this.obj = obj;
				this.id = id;
			}
		}

		Field holder = null;
		ArrayList<Element> datas = new ArrayList<>();
	}

	private static long elapse = 0;
	private static final String LogTag = "ViewInject";
	private static final Resources res;
	private static final String pkgName;
	private static HashMap<Class<?>, Method> MethodMapFindView = new HashMap<>();
	private static HashMap<Class<?>, Method> MethodMapSetOnClick = new HashMap<>();
	private static HashMap<Class<?>, InjectData> InjectMap = new HashMap<>();

	static
	{
		res = BaseApplication.getContext().getResources();
		pkgName = BaseApplication.getContext().getPackageName();
	}

	private static Method loadMethFindView(Class<?> clz)
	{
		Method meth = MethodMapFindView.get(clz);
		if (meth != null)
			return meth;
		// load method
		Log.d(LogTag, "load method of findViewById: " + clz.getName());
		try
		{
			meth = clz.getMethod("findViewById", int.class);
			MethodMapFindView.put(clz, meth);
		}
		catch (NoSuchMethodException e)
		{
			Log.e(LogTag, "error when load method of findViewById: " + clz.getName(), e);
		}
		return meth;
	}

	private static Method loadMethSetOnClick(Class<?> clz)
	{
		Method meth = MethodMapSetOnClick.get(clz);
		if (meth != null)
			return meth;
		// load method
		Log.d(LogTag, "load method of setOnClickListener: " + clz.getName());
		try
		{
			meth = clz.getMethod("setOnClickListener", OnClickListener.class);
			MethodMapSetOnClick.put(clz, meth);
		}
		catch (NoSuchMethodException e)
		{
			Log.e(LogTag, "error when load method of setOnClickListener: " + clz.getName(), e);
		}
		return meth;
	}

	private static Field loadHolder(Class<?> clz) throws NoSuchFieldException
	{
		ObjView ov = clz.getAnnotation(ObjView.class);
		if (ov == null)
			return null;
		String holderName = ov.value();
		if (holderName == "")// default value
			return null;
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
		return holder;
	}

	private static InjectData loadInjectData(Class<?> clz)
			throws NoSuchMethodException, NoSuchFieldException
	{
		InjectData ret = InjectMap.get(clz);
		if (ret != null)
			return ret;
		// load holder
		ret = new InjectData();
		try
		{
			ret.holder = loadHolder(clz);
		}
		catch (NoSuchFieldException e)
		{
			Log.e(LogTag, "error when load holder for " + clz.getName(), e);
		}
		// load field
		Log.d(LogTag, "load field: " + clz.getName());

		Field[] fields = clz.getDeclaredFields();
		for (Field f : fields)
		{
			BindView b = f.getAnnotation(BindView.class);
			if (b != null)
			{
				f.setAccessible(true);
				int val = b.value();
				if (val == 0)
					val = res.getIdentifier(f.getName(), "id", pkgName);
				if (val != 0)
				{
					InjectData.Element ele = new InjectData.Element(f, val);
					if (b.onClick() != "")
					{
						ele.bindClick = b.onClick() == "this" ? InjectData.Element.BindState.self
								: InjectData.Element.BindState.other;
						ele.setOnClick = loadMethSetOnClick(f.getType());
					}
					ret.datas.add(ele);
				}
				else
					Log.e(LogTag, "canot get R.id for " + f.getName() + " in " + clz.getName());
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
			InjectData injDat = loadInjectData(clz);
			try
			{
				Object viewHolder = (injDat.holder == null ? obj : injDat.holder.get(obj));
				Class<?> holderClz = viewHolder.getClass();
				Method meth = loadMethFindView(holderClz);
				for (InjectData.Element inj : injDat.datas)
				{
					try
					{
						Object view = meth.invoke(viewHolder, inj.id);
						try
						{
							inj.obj.set(obj, view);
						}
						catch (IllegalAccessException | IllegalArgumentException e)
						{
							Log.e(LogTag,
									"error when do inject\n" + inj.obj.getName() + " <== " + view,
									e);
						}
						if (inj.bindClick == InjectData.Element.BindState.self)
						{
							try
							{
								inj.setOnClick.invoke(view, obj);
							}
							catch (IllegalAccessException | IllegalArgumentException e)
							{
								Log.e(LogTag, "error when do setonclick\n" + obj + " ==> " + view,
										e);
							}
						}
					}
					catch (InvocationTargetException e)
					{
						Log.e(LogTag, "error when findViewById on " + viewHolder + " ==> " + inj.id,
								e);
					}
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
