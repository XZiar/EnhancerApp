package xziar.enhancer.adapter;

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
import xziar.enhancer.adapter.CommonHolder.OnItemClickListener;

public class CommonAdapter<TD, TH extends CommonHolder<TD>> extends RecyclerView.Adapter<TH>
		implements Comparator<TD>, OnClickListener
{
	private static final String LogTag = "CommonAdapter";
	private OnItemClickListener<TD> itemClick;
	private TH header, footer;
	protected ArrayList<Integer> resID = new ArrayList<>();
	private Constructor<TH> THcon;
	private HashMap<TH, TD> mapping = new HashMap<>();
	private HashMap<TD, Integer> types = new HashMap<>();
	protected ArrayList<TD> datas = new ArrayList<>();
	protected LayoutInflater inflater;

	public CommonAdapter(Context context, Class<TH> THclass)
	{
		inflater = LayoutInflater.from(context);
		try
		{
			THcon = THclass.getConstructor(View.class);
		}
		catch (NoSuchMethodException e)
		{
			Log.e(LogTag, "cannot load default constructor of ViewHolder:" + THclass.getName(), e);
		}
	}

	protected TH genHolder(View view)
	{
		try
		{
			TH holder = THcon.newInstance(view);
			return holder;
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
		{
			Log.w(LogTag, "failed when create new instance of ViewHolder", e);
			return null;
		}
	}

	public void setHeaderFooter(View header, View footer)
	{
		this.header = (header == null ? null : genHolder(header));
		this.footer = (footer == null ? null : genHolder(footer));
		notifyDataSetChanged();
	}

	public void setHeaderView(View header)
	{
		this.header = (header == null ? null : genHolder(header));
		notifyDataSetChanged();
	}

	public void setFooterView(View footer)
	{
		this.footer = (footer == null ? null : genHolder(footer));
		notifyDataSetChanged();
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
		int pos = position - (header == null ? 0 : 1);
		if (pos < 0)// header
			return -1;
		if (pos >= datas.size())// footer
			return -2;
		TD item = datas.get(pos);
		Integer type = types.get(item);
		return type == null ? 0 : type;
	}

	public int pos2idx(int pos)
	{
		pos -= (header == null ? 0 : 1);
		if (pos >= datas.size())
			return -2;
		else
			return -1;
	}

	public int idx2pos(int idx)
	{
		if (idx == -1 || idx < -2)
			return 0;
		if (idx == -2)
			return getItemCount();
		if (idx < datas.size())
			return idx + (header == null ? 0 : 1);
		else
			return getItemCount();
	}

	@Override
	public int getItemCount()
	{
		return datas.size() + (header == null ? 0 : 1) + (footer == null ? 0 : 1);
	}

	@Override
	public void onBindViewHolder(TH holder, int position)
	{
		int pos = position - (header == null ? 0 : 1);
		if (pos >= 0 && pos < datas.size())
		{
			TD item = datas.get(pos);
			mapping.put(holder, item);
			holder.setData(item, pos, getItemViewType(pos));
		}
	}

	@Override
	public TH onCreateViewHolder(ViewGroup parent, int viewType)
	{
		if (viewType == -1)
			return header;
		if (viewType == -2)
			return footer;
		View view = inflater.inflate(resID.get(viewType), parent, false);
		view.setOnClickListener(this);
		return genHolder(view);
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
