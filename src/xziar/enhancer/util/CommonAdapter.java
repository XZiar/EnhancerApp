package xziar.enhancer.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommonAdapter<TD, TH extends CommonAdapter.CommonHolder<TD>>
		extends RecyclerView.Adapter<TH> implements Comparator<TD>
{
	static abstract class CommonHolder<T> extends RecyclerView.ViewHolder
	{
		public CommonHolder(View itemView)
		{
			super(itemView);
		}

		abstract public void setData(T data);

	}

	protected int resID;
	private Constructor<?> THcon;
	protected ArrayList<TD> datas = new ArrayList<>();
	protected LayoutInflater inflater;

	public CommonAdapter(Context context, Class<?> THclass)
	{
		inflater = LayoutInflater.from(context);
		try
		{
			THcon = THclass.getConstructor(View.class);
		}
		catch (NoSuchMethodException e)
		{
			Log.e("Adapter", e.getLocalizedMessage(), e);
		}
	}

	public void refresh(Collection<? extends TD> datas)
	{
		this.datas.clear();
		this.datas.addAll(datas);
		Log.v("refresh", "datas size : " + this.datas.size());
		Collections.sort(this.datas, this);
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount()
	{
		return datas.size();
	}

	@Override
	public void onBindViewHolder(TH holder, int position)
	{
		TD item = datas.get(position);
		holder.setData(item);
	}

	@Override
	public TH onCreateViewHolder(ViewGroup parent, int position)
	{
		View view = inflater.inflate(resID, parent, false);
		try
		{
			@SuppressWarnings("unchecked")
			TH holder = (TH) THcon.newInstance(view);
			return holder;
		}
		catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public int compare(TD lhs, TD rhs)
	{
		return 0;
	}

}
