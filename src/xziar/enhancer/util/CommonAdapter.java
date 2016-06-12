package xziar.enhancer.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import xziar.enhancer.util.CommonHolder.OnItemClickListener;

public class CommonAdapter<TD, TH extends CommonHolder<TD>>
		extends RecyclerView.Adapter<TH>
		implements Comparator<TD>, OnClickListener
{
	private OnItemClickListener<TD> itemClick;
	protected int resID;
	private Constructor<?> THcon;
	private HashMap<TH, Integer> mapping = new HashMap<>();
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

	public void refresh(ArrayList<TD> datas)
	{
		this.datas = datas;
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
		mapping.put(holder, position);
		TD item = datas.get(position);
		holder.setData(item);
	}

	@Override
	public TH onCreateViewHolder(ViewGroup parent, int position)
	{
		View view = inflater.inflate(resID, parent, false);
		view.setOnClickListener(this);
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

	public void setItemClick(OnItemClickListener<TD> itemClick)
	{
		this.itemClick = itemClick;
	}

	@Override
	public void onClick(View v)
	{
		@SuppressWarnings("unchecked")
		TH holder = (TH) v.getTag();
		TD data = datas.get(mapping.get(holder));
		if (itemClick != null)
			itemClick.OnClick(data);
	}

}
