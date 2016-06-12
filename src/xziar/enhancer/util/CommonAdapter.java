package xziar.enhancer.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

public class CommonAdapter<TD, TH extends CommonHolder<TD>> extends
		RecyclerView.Adapter<TH> implements Comparator<TD>, OnClickListener
{
	private OnItemClickListener<TD> itemClick;
	protected ArrayList<Integer> resID = new ArrayList<>();
	private Constructor<?> THcon;
	private HashMap<TH, TD> mapping = new HashMap<>();
	private HashMap<TD, Integer> types = new HashMap<>();
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
	public int getItemViewType(int position)
	{
		TD item = datas.get(position);
		Integer type = types.get(item);
		return type == null ? 0 : type;
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
		mapping.put(holder, item);
		holder.setData(item);
	}

	@Override
	public TH onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = inflater.inflate(resID.get(viewType), parent, false);
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
		TD data = mapping.get(holder);
		if (itemClick != null)
			itemClick.OnClick(data);
	}

	public int changeViewType(TD item)
	{
		if (!datas.contains(item))
		{
			types.remove(item);
			return -1;
		}
		Integer type = types.get(item);
		if (type == null)
			type = 0;
		type = (type + 1) % resID.size();
		types.put(item, type);
		notifyDataSetChanged();
		return type;
	}
}
